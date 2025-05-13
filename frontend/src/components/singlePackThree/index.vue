<template>
  <div id="containerLoop" @mousemove="mouseMove">
    <div class="index">{{ state.curCabinetIndex + 1 }}</div>
    <img src="../../assets/img/arrowLeft.png" @click="nextCab('plus')" class="arrowLeft">{{ state.curCabinetIndex
    }}</img>
    <img src="../../assets/img/arrowRight.png" @click="nextCab('add')" class="arrowRight">{{ state.curCabinetIndex
    }}</img>

    <div class="status-panel">
      <div class="btn " :class="{ active: status === 'start' }" @click="changeMode('start')">开始</div>
      <div class="btn " :class="{ active: status === 'stop' }" @click="changeMode('stop')">停止</div>

    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, defineEmits, watch, nextTick, onBeforeUnmount, ref } from 'vue';

import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { Reflector } from 'three/examples/jsm/objects/Reflector.js';
import { addFence } from '../../tools/addFence.ts';

import { color, time } from 'echarts';
import store from '../../store/index';
import { GetCellMetrics } from '../../api/packApi.ts';

const emit = defineEmits(['customEvent']);
var timer;
// 用于保存上一个高亮的对象
let previousHighlightedNode = null;

const state = reactive({
  planePos: {
    left: 0,
    top: 0,
  },
  planeDisplay: 'block',
  curCabinet: {
    name: 'Loading……',
    temperature: 0,
    capacity: 0,
    count: 0,
  },
  cabinets: [], // 存储所有机柜信息
  curCabinetIndex: 0, // 当前显示的机柜索引
});
const status = ref('start')

let mesh = null;
let camera = null;
let scene = null;
let renderer = null;
let controls = null;
let label = null; //标签
let currentModel = null; // 存储当前加载的模型
let maps = new Map();
let curCabinet = null;
let innerRing, outerRing, wavematerial;

const crtTexture = (imgName) => {
  let curTexture = maps.get(imgName);
  if (!curTexture) {
    curTexture = new THREE.TextureLoader().load('./dainchi/' + imgName);
    curTexture.flipY = false;
    curTexture.wrapS = 1000;
    curTexture.wrapT = 1000;
    maps.set(imgName, curTexture);
  }
  return curTexture;
};
const changeMode = (type) => {
  if (type === 'start') {
    status.value = 'start';
    console.log('开始');
    timer = setInterval(updateLabel, 1000 * 10);

  } else if (type === 'stop') {
    status.value = 'stop';
    console.log('停止');
    clearInterval(timer);

  }
}
const nextCab = (type) => {
  if (type === 'add') {
    if (state.curCabinetIndex+1 < 52 ) {
      state.curCabinetIndex += 1;
    } else {
      state.curCabinetIndex = 51; // 可选，确保不会超出
    }
    // 如果存在上一个高亮对象，将其颜色恢复
    state.cabinets.forEach((item) => {

      item.obj.children[0].material = new THREE.MeshBasicMaterial({
        color: 0x1a1a1a, // 恢复默认颜色
        map: null, // 移除贴图
      });
      item.obj.children[1].material = new THREE.MeshBasicMaterial({
        color: 0x999999,
        map: null,
      });
    });
    if (state.cabinets[state.curCabinetIndex].obj) {
      highlightNodeWithOutline(state.cabinets[state.curCabinetIndex].obj);
    }
    //获取单个电池数据
    GetCellMetricsData(state.curCabinetIndex);
  } else if (type === 'plus' ) {
    console.log(type,state.curCabinetIndex, '减少');
    
    if (state.curCabinetIndex+1 > 1) {
      state.curCabinetIndex -= 1;
    } else {
      state.curCabinetIndex = 0;
    }
    // 如果存在上一个高亮对象，将其颜色恢复
    state.cabinets.forEach((item) => {

      item.obj.children[0].material = new THREE.MeshBasicMaterial({
        color: 0x1a1a1a, // 恢复默认颜色
        map: null, // 移除贴图
      });
      item.obj.children[1].material = new THREE.MeshBasicMaterial({
        color: 0x999999,
        map: null,
      });
    });
    if (state.cabinets[state.curCabinetIndex].obj) {
      highlightNodeWithOutline(state.cabinets[state.curCabinetIndex].obj);
    }
    //获取单个电池数据
    GetCellMetricsData(state.curCabinetIndex);
  }
};
const init = () => {
  createScene();
  loadGLTF(store.state.CurrentModelName);
  createCamera();
  createRender();
  createControls();
  render();
  animate();
};

const createScene = async () => {
  scene = new THREE.Scene();

  // 2. 创建内环
  const innerTexture = new THREE.TextureLoader().load('/ground/innerring.png');
  const innerMaterial = new THREE.MeshBasicMaterial({
    map: innerTexture,
    color: 0x2a4a81, // 颜色叠加（青色）

    transparent: true,
  });
  innerRing = new THREE.Mesh(new THREE.RingGeometry(10, 20, 64), innerMaterial);
  innerRing.rotation.x = -Math.PI / 2;
  scene.add(innerRing);

  // 3. 创建外环
  const outerTexture = new THREE.TextureLoader().load('/ground/outerring.png');
  const outerMaterial = new THREE.MeshBasicMaterial({
    map: outerTexture,
    color: 0x2a4a81, // 颜色叠加（青色）
    transparent: true,
  });
  outerRing = new THREE.Mesh(new THREE.RingGeometry(21, 24, 64), outerMaterial);
  outerRing.rotation.x = -Math.PI / 2;
  scene.add(outerRing);

  // 创建平面几何体
  const geometry = new THREE.PlaneGeometry(1000, 1000);
  const loader = new THREE.TextureLoader();
  const textureUrls = [
    '/ground/digitalGround1.png',
    '/ground/digitalGround2.png',
    '/ground/digitalGround3.png',
    '/ground/digitalGround4.png',
  ];
  const textures = await Promise.all(
    textureUrls.map(
      (url) =>
        new Promise((resolve, reject) => {
          loader.load(
            url,
            (texture) => {
              texture.colorSpace = THREE.LinearSRGBColorSpace;
              texture.wrapS = THREE.RepeatWrapping;
              texture.wrapT = THREE.RepeatWrapping;
              texture.magFilter = THREE.LinearFilter;
              texture.minFilter = THREE.LinearMipmapLinearFilter;
              resolve(texture);
            },
            undefined,
            (err) => reject(err)
          );
        })
    )
  ).catch((err) => {
    console.error('Texture load failed:', err);
  });

  // const texture = new THREE.TextureLoader().load('/ground/floorboard.png');
  // texture.wrapS = THREE.RepeatWrapping;
  // texture.wrapT = THREE.RepeatWrapping;
  // 创建材质
  wavematerial = new THREE.ShaderMaterial({
    side: THREE.DoubleSide,
    transparent: true,
    blending: THREE.AdditiveBlending, // 添加混合模式让效果更亮
    uniforms: {
      time: { value: 0.0 },
      radius: {
        value: 100,
      },
      uColor: {
        value: new THREE.Color('#45aefd'),
      },
      // uScanTex: { value: texture },
      // uScanColor: { value: new THREE.Color(0x2a4a81) }, // 主要扫描颜色
      // uScanColorDark: { value: new THREE.Color(0x2a4a81) }, // 暗部扫描颜色
      // uRadius: { value: 1.0 }, // 新增的半径变量
      texture0: {
        value: textures[0],
      },
      texture1: {
        value: textures[1],
      },
      texture2: {
        value: textures[2],
      },
      texture3: {
        value: textures[3],
      },
    },
    vertexShader: `
        varying vec2 vUv;
        varying vec3 vPosition;
        void main() {
            vUv = uv;
            vPosition = position;
            gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
        }
    `,
    fragmentShader: `
        uniform float time;
        uniform float radius;

        uniform sampler2D texture0;
        uniform sampler2D texture1;
        uniform sampler2D texture2;
        uniform sampler2D texture3;

        varying vec3 vPosition;
        uniform vec3 uColor;
        varying vec2 vUv;

        float wave(float a, float l, float s, float second, float val) {
            float PI = 3.141592653;
            float wave = a * sin(- val * 2.0 * PI / l + second * s * 2.0 * PI / l);
            return (wave + 1.0) / 2.0;
        }
        void main(){
            vec4 basceColor = vec4(uColor, 1.0);
            vec4 back = texture2D( texture0, vUv * 16.0);

            vec4 ori1 = texture2D( texture1, vUv * 4.0); // 电子元件
            vec4 ori2 = texture2D( texture2, vUv * 16.0 ); // 点
            vec4 ori3 = texture2D( texture3, vUv * 16.0 ); // 网格

            float length = length( vec2(vPosition.x, vPosition.y) );
            // 应用波函数蒙版
            float flag1 = wave(1.0, radius / 2.0, 45.0, time, length);
            if (flag1 < 0.5) {
                flag1 = 0.0;
            }
            ori1.a = ori1.a * (flag1 * 0.8 + 0.2);
            float flag2 = wave(1.0, radius / 3.0, 30.0, time, length);
            ori2.a = ori2.a * (flag2 * 0.8 + 0.2);
            float flag3 = wave(1.0, 60.0, 20.0, time, length);
            ori3.a = ori3.a * (flag3 * 2.0 - 1.5);
            // 应用蒙版
            float alpha = clamp(ori1.a + ori2.a + ori3.a + back.a * 0.01, 0.0, 1.0);
            basceColor.a = alpha*2.0;

            gl_FragColor = basceColor * clamp((2.0 - (length * 2.0 / radius)), 0.0, 1.0);
        }
    `,
  });

  // 创建网格并添加到场景
  const mesh = new THREE.Mesh(geometry, wavematerial);
  mesh.rotation.x = Math.PI / 2;
  mesh.position.y = 1;

  scene.add(mesh);

  const light = new THREE.AmbientLight(0xffffff, 1); // 柔和环境光
  scene.add(light);

  const directionalLight = new THREE.DirectionalLight(0xffffff, 1); // 强烈的方向光
  directionalLight.position.set(0, 0, 0);
  // scene.add(directionalLight);

  // addFence(scene);
};

// 定时显示下一个机柜
const updateLabel = () => {
  // console.log(state.cabinets[state.curCabinetIndex], '当前电池');

  if (state.curCabinetIndex < state.cabinets.length) {
    const pos = state.cabinets[state.curCabinetIndex]?.pos;
    // console.log(state.cabinets[state.curCabinetIndex].obj, '当前电池');
    // 如果存在上一个高亮对象，将其颜色恢复
    if (previousHighlightedNode) {
      // console.log(previousHighlightedNode, 'previousHighlightedNode');

      // previousHighlightedNode.material = new THREE.MeshBasicMaterial({
      //   map: new THREE.TextureLoader().load('/dianchi/silver.png'),
      // });

      previousHighlightedNode.children[0].material = new THREE.MeshBasicMaterial({
        color: 0x1a1a1a, // 恢复默认颜色
        map: null, // 移除贴图
      });

      previousHighlightedNode.children[1].material = new THREE.MeshBasicMaterial({
        color: 0x999999,
        map: null,
      });
      previousHighlightedNode.children[0].material.color.set(0x1a1a1a);

      previousHighlightedNode.children[1].material.color.set(0x999999);
    }
    // state.curCabinetIndex = state.cabinets[state.curCabinetIndex]?.name;
    state.curCabinetIndex++;

    // label.element.innerHTML = state.cabinets[state.curCabinetIndex]?.name || '';
    // // console.log(pos, '当前模型位置');

    // label.position.set(pos.x, pos.y, 5);
    // 使用 outline 高亮
    if (state.cabinets[state.curCabinetIndex].obj) {
      highlightNodeWithOutline(state.cabinets[state.curCabinetIndex].obj);
    }
    // 保存当前高亮对象
    previousHighlightedNode = state.cabinets[state.curCabinetIndex].obj;
    //获取单个电池数据
    GetCellMetricsData(state.curCabinetIndex);
  } else {
    state.curCabinetIndex = 0;
  }
};
const changeGltf = (modelUrl) => {
  state.cabinets = [];
  state.curCabinetIndex = 0;
};
//高亮相关
const highlightNodeWithOutline = (node) => {
  // console.log(node, '修改材质颜色');
  // 一个模型对象
  // 确保 node 是有效的
  state.cabinets.forEach((item) => {

    item.obj.children[0].material = new THREE.MeshBasicMaterial({
      color: 0x1a1a1a, // 恢复默认颜色
      map: null, // 移除贴图
    });
    item.obj.children[1].material = new THREE.MeshBasicMaterial({
      color: 0x999999,
      map: null,
    });
  });
  if (node) {


    // node.children[1].material.color.set(0xffac0c);
    node.children[0].material = new THREE.MeshBasicMaterial({
      color: 0xffac0c, // 恢复默认颜色
      map: null, // 移除贴图
    });


    //设置为红色，可以根据需要修改颜色
  }
};
const loadGLTF = (modelUrl = 52) => {
  console.log(modelUrl, 'modelUrl');

  if (currentModel) {
    scene.remove(currentModel); // 先删除旧模型

    currentModel = null;
  }
  if (label) {
    scene.remove(label); // 先删除旧模型
  }
  const loader = new GLTFLoader();
  // ./model/machineRoom.gltf
  loader.load(`./dianchi/${modelUrl}.gltf`, (gltf) => {
    const { children } = gltf.scene; // 获取子对象
    // console.log(...children);

    let model = gltf.scene; // 获取整个GLTF模型

    model.scale.set(20, 20, 20); // 放大 2 倍
    model.rotation.set(0, Math.PI / 2, 0); // 绕Y轴旋转 90 度
    model.position.y = -1;
    model.position.x = -1;
    model.position.z = 2;
if(modelUrl==52){
  children.forEach((child) => {
      child.name = child.name.replace(/^obj_/, ''); // 去掉前缀
    });
}else{
  children.forEach((child) => {
      child.name = child.name.replace(/^obj/, ''); // 去掉前缀
    });
}

 
    // 排序 cabinets 数组，根据 name 进行排序
    children.sort((a, b) => {
      const numA = parseInt(a.name, 10); // 将字符串转为数字
      const numB = parseInt(b.name, 10); // 同上

      return numA - numB; // 按数字大小排序
    });

    children.forEach((obj) => {
      // console.log(obj.material.color);

      const screenPosition = obj.children[0].getWorldPosition(new THREE.Vector3());

      // 去掉 obj_ 前缀

      state.cabinets.push({
        obj: obj,
        name: obj.name,
        pos: {
          x: screenPosition.x,
          y: screenPosition.y,
          z: screenPosition.z,
        },
      });
    });

    changeMat();

    scene.add(model);
    currentModel = model; // 记录当前模型

  });
};

const createCamera = () => {
  const element = document.getElementById('containerLoop');
  console.log(element.clientWidth, element.clientHeight);

  const width = element.clientWidth;
  const height = element.clientHeight;
  const k = width / height;
  camera = new THREE.PerspectiveCamera(45, k, 0.1, 1000);

  // camera.position.set(
  //   23.792756311814856, 24.96364603000248, -12.884570862422876)
  camera.position.copy(
    new THREE.Vector3(10.032411550900214, 15.523325607382143, -17.37892684473546)
  );

  camera.lookAt(0, 0, 0);
  scene.add(camera);
};

const createRender = () => {
  const element = document.getElementById('containerLoop');
  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });

  renderer.setSize(element.clientWidth, element.clientHeight);
  renderer.setClearColor(0x000101, 1);
  element.appendChild(renderer.domElement);
};

const render = () => {
  renderer.render(scene, camera);
  requestAnimationFrame(render);
};

const createControls = () => {
  controls = new OrbitControls(camera, renderer.domElement);
  controls.maxPolarAngle = (Math.PI / 2) * 0.8; //限制上下旋转范围 不看到底部
  // 限制缩放等级
  controls.minDistance = 1; // 设置最小缩放距离
  controls.maxDistance = 2000; // 设置最大缩放距离
  // controls.addEventListener('change', () => {
  //   const pos = camera.position;
  //   console.log('相机xyz坐标', [pos.x, pos.y, pos.z]);
  //   const target = controls.target;
  //   console.log('目标观察点xyz坐标', [target.x, target.y, target.z]);
  // });
};

const changeMat = () => {
  console.log(
    state.cabinets.map((item) => item),
    '左上角cabinets'
  );
  state.cabinets[0].obj.children[0].material = new THREE.MeshBasicMaterial({
    color: 0xffac0c, // 恢复默认颜色
    map: null, // 移除贴图
  });

  state.cabinets.forEach((obj) => {
    // obj.material = new THREE.MeshBasicMaterial({
    //   map: new THREE.TextureLoader().load('/dianchi/pack.png'),
    // });
  });
};
const handleDoubleClick = () => {
  if (curCabinet) {
    curCabinet.onDoubleClick();
    console.log('双击了机柜:', curCabinet.name);
    emit('customEvent', 'DataView');
  }
};
const selectCabinet = (x, y) => {
  const { width, height } = renderer.domElement;
  //射线投射器，可基于鼠标点和相机，在世界坐标系内建立一条射线，用于选中模型
  const raycaster = new THREE.Raycaster();
  //鼠标在裁剪空间中的点位
  const pointer = new THREE.Vector2();

  // 鼠标的canvas坐标转裁剪坐标
  pointer.set((x / width) * 2 - 1, -(y / height) * 2 + 1);
  // 基于鼠标点的裁剪坐标位和相机设置射线投射器
  raycaster.setFromCamera(pointer, camera);
  // 选择机柜
  const intersect = raycaster.intersectObjects(cabinets)[0];
  let intersectObj = intersect ? intersect.object : null;

  // 若之前已有机柜被选择，且不等于当前所选择的机柜，取消之前选择的机柜的高亮
  if (curCabinet && curCabinet !== intersectObj) {
    const material = curCabinet.material;
    curCabinet.onDoubleClick = () => {
      console.log('双击了机柜:', curCabinet.name);
    };

    // 先解绑，防止重复绑定
    window.removeEventListener('dblclick', handleDoubleClick);
    window.addEventListener('dblclick', handleDoubleClick);
    material.setValues({
      map: new THREE.TextureLoader().load('/model/cabinet.jpg'),
    });
  }
  /*
        若当前所选对象不为空：
          触发鼠标在机柜上移动的事件。
          若当前所选对象不等于上一次所选对象：
            更新curCabinet。
            将模型高亮。
            触发鼠标划入机柜事件。
        否则若上一次所选对象存在：
          置空curCabinet。
          触发鼠标划出机柜事件。
      */
  if (intersectObj) {
    onMouseMoveCabinet(x, y);
    if (intersectObj !== curCabinet) {
      curCabinet = intersectObj;
      const material = intersectObj.material;
      material.setValues({
        map: new THREE.TextureLoader().load('/model/cabinet-hover.jpg'),
      });
      onMouseOverCabinet(intersectObj);
    }
  } else if (curCabinet) {
    curCabinet = null;
    onMouseOutCabinet();
  }
};

const mouseMove = ({ clientX, clientY }) => {
  // selectCabinet(clientX, clientY);
};

const onMouseOverCabinet = (cabinet) => {
  console.log(cabinet.name, cabinet);
  state.planeDisplay = 'block';
  // getCabinateByName(cabinet.name).then(({ data }) => {
  //   state.curCabinet = { ...data, name: cabinet.name };
  // });
};

const onMouseMoveCabinet = (x, y) => {
  state.planePos.left = x + 'px';
  state.planePos.top = y + 'px';
};

const onMouseOutCabinet = () => {
  state.planeDisplay = 'none';
};

const getCabinateByName = (name) => {
  let path = 'http://127.0.0.1:4523/m1/2003080-0-default/name/';
  return fetch(path + name).then((res) => res.json());
};
// 4. 让圆环旋转
function animate() {
  requestAnimationFrame(animate);

  innerRing.rotation.z += 0.001; // 内环顺时针转
  outerRing.rotation.z -= 0.001; // 外环逆时针转（速度稍慢）
  // console.log(wavematerial);
  if (wavematerial) {
    wavematerial.uniforms.time.value += 0.01;
  }
  renderer.render(scene, camera);
}
watch(
  () => window.nowPage,
  (newVal, oldVal) => {
    nextTick().then(() => {
      init();
      window.nowPage = 'ThreeView';
    });
  },
  { immediate: true }
);

//获取cell级别数据 集装箱 规格 cellid
const GetCellMetricsData = async (cellId) => {
  let res = await GetCellMetrics(
    store.state.CurrentContainer.id,
    store.state.CurrentModelName,
    cellId||1,
    {
      dateFrom: store.state.dateFrom,
      dateTo: store.state.dateTo,
    }
  );
  store.commit('setCurrentCellData', res);
};
watch(
  () => store.state.dateTo,
  (newVal, oldVal) => {
    console.log(newVal,'dateTo变化了');
    GetCellMetricsData();
  },
  { immediate: true }
);
onMounted(() => {
  // init();
  // window.nowPage = 'ThreeView';
  // 每 1 秒更新标签位置
  timer = setInterval(updateLabel, 1000 * 25);
});

function disposeScene(scene, renderer) {
  if (!scene) return;

  // 递归清理场景中的对象
  scene.traverse((obj) => {
    if (obj.geometry) {
      obj.geometry.dispose();
    }

    if (obj.material) {
      if (Array.isArray(obj.material)) {
        obj.material.forEach((mat) => mat.dispose());
      } else {
        obj.material.dispose();
      }
    }

    if (obj.texture) {
      obj.texture.dispose();
    }
  });

  // 清空场景
  while (scene.children.length > 0) {
    let obj = scene.children[0];
    scene.remove(obj);
  }

  // 释放渲染器资源
  if (renderer) {
    renderer.dispose();
  }
}

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer);
  }
  disposeScene(scene, renderer);
});
</script>

<style lang="scss" scoped>
.status-panel {
  position: absolute;
  top: 0;
  right: 0;
  display: flex;
  gap: 0.06rem;
  align-items: center;
  padding: 5px 10px;
  border-radius: 4px;
  color: #fff;
  font-family: Arial, sans-serif;

  .btn {
    height: 0.15rem;
    line-height: 0.1rem;
    text-align: center;
    margin-left: 5px;
    padding: 5px 10px;
    font-size: 0.08rem;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    background: #0D0D0D;
    border: 0.01rem solid #fff;

  }

  .active {
    background: url('../../assets/img/green.png') no-repeat 100% 100% / 100% 100%;

  }
}

#containerLoop {
  position: relative;
  width: 100%;
  height: 100%;

  .index {
    width: 0.4rem;
    height: 0.4rem;
    display: flex;
    font-size: 0.2rem;
    align-items: center;
    justify-content: center;
    background: #2cbeff;
    position: absolute;
    top: 0;
  }

  .arrowLeft {
  width:0.2rem;
    position: absolute;
    bottom: 0;
    right: 0.25rem;
    cursor: pointer;
  }

  .arrowRight {
  width:0.2rem;

    position: absolute;
    bottom: 0;
    right: 0;
    cursor: pointer;
  }
}

#plane {
  position: absolute;
  top: 0;
  left: 0;

  background-color: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 0 18px;
  transform: translate(12px, -100%);
  text-align: left;
}
</style>
