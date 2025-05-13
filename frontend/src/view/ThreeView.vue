<template>
  <div id="container" @mousemove="mouseMove">
    <div
      id="plane"
      :style="{
        left: state.planePos.left,
        top: state.planePos.top,
        display: state.planeDisplay,
      }"
    >
      <p style="font-size: 0.08rem">pack名称：{{ curCabinet?.name || '' }}</p>
    </div>

    <!-- <img src="../assets/img/arrowLeft.png" @click="changeGltf('single52')" class="arrowLeft">{{ state.curCabinetIndex
    }}</img>
    <img src="../assets/img/arrowRight.png" @click="changeGltf('single104')" class="arrowRight">{{ state.curCabinetIndex
    }}</img>   -->
  </div>
  <Pannel
    :overViewData="DataStatistics"
    :alarmData="alarmData"
    :containerData="containerData"
  />
  <TimeChose />
</template>

<script setup>
import {
  onMounted,
  reactive,
  defineEmits,
  watch,
  nextTick,
  ref,
  onBeforeUnmount,
  computed,
} from 'vue';
import Pannel from '../components/layout/index.vue';
import TimeChose from '../components/TimeChose.vue';

import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
import { Reflector } from 'three/examples/jsm/objects/Reflector.js';
import { addFence } from '../tools/addFence.ts';
import store from '../store/index';

import {
  GetSystemConfiguration,
  GetOverViewData,
  GetPackMetrics,
} from '../api/packApi.ts';

const emit = defineEmits(['customEvent']);

const state = reactive({
  planePos: {
    left: 0,
    top: 0,
  },
  planeDisplay: 'none',
  curCabinet: {
    name: 'Loading……',
    temperature: 0,
    capacity: 0,
    count: 0,
  },
});

let mesh = null;
let camera = null;
let scene = null;
let renderer = null;
let controls = null;
let currentModel = null; //存储当前模型

let maps = new Map();
let cabinets = [];
let curCabinet = null;
let originalMaterial = null;

let innerRing, outerRing, wavematerial;

const DataStatistics = ref(null);
const alarmData = ref(null);
const containerData = ref(null);

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
const changeGltf = (modelUrl) => {
  state.cabinets = [];
  // state.curCabinetIndex = 0;
  loadGLTF(modelUrl);
  store.commit('setCurrentModelName', modelUrl == 'single52' ? 52 : 104);
};
const init = () => {
  createScene();
  createCamera();
  createRender();
  createControls();
  render();
  animate();
};
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

const createScene = async () => {
  scene = new THREE.Scene();
  // // 1. 创建地板
  // const floorTexture = new THREE.TextureLoader().load('/ground/componentGround.png');
  // const floorMaterial = new THREE.MeshBasicMaterial({
  //   map: floorTexture,
  //   color: 0x2a4a81,
  //   metalness: 0.8, // 让地板接近金属
  //   roughness: 0.2, // 让地板更光滑，降低粗糙度
  //   transparent: true,
  // });
  // const floor = new THREE.Mesh(new THREE.PlaneGeometry(80, 80, 64), floorMaterial);
  // floor.rotation.x = -Math.PI / 2;
  // floor.position.y = -0.1;

  // scene.add(floor);

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
        value: 100.0,
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

  const light = new THREE.AmbientLight(0xffffff, 0.5); // 柔和环境光
  scene.add(light);

  const directionalLight = new THREE.DirectionalLight(0xffffff, 1); // 强烈的方向光
  directionalLight.position.set(10, 20, 10);
  scene.add(directionalLight);

  addFence(scene);
};
const clock = new THREE.Clock();
let mixer;
const loadGLTF = (modelUrl) => {
  console.log(modelUrl, '首页模型');

  if (currentModel) {
    scene.remove(currentModel); // 先删除旧模型

    currentModel = null;
  }
  const loader = new GLTFLoader();
  // ./model/machineRoom.gltf
  loader.load(`./dianchi/${modelUrl}.gltf`, (gltf) => {
    const { children } = gltf.scene; // 获取子对象
    // console.log(...children);

    const { scene: model, animations } = gltf;
    model.scale.set(2, 2, 2); // 放大 2 倍
    model.rotation.set(0, -Math.PI / 2, 0); // 绕Y轴旋转 90 度
    model.position.y = 0;
    model.position.x = 10;
    // 确保变换应用到模型
    model.updateMatrixWorld(); // 更新世界矩阵
    children.forEach((obj) => {
      if (
        !obj.name.includes('obj') &&
        !obj.name.includes('Line') &&
        !obj.name.includes('Box') &&
        !obj.name.includes('h') &&
        !obj.name.includes('jsj')
      ) {
        cabinets.push(obj);
      }
    });

    scene.add(model);
    currentModel = model;

    // 简单的动画播放
    if (gltf.animations.length > 0) {
      mixer = new THREE.AnimationMixer(model);
      const action = mixer.clipAction(gltf.animations[0]);
      const action1 = mixer.clipAction(gltf.animations[1]);

      action.setLoop(THREE.LoopRepeat); // 设置循环播放
      action1.setLoop(THREE.LoopRepeat); // 设置循环播放

      action.play();
      action1.play();
    }

    changeMat();
  });
};

const createCamera = () => {
  const element = document.getElementById('container');
  console.log(element.clientWidth, element.clientHeight);

  const width = element.clientWidth;
  const height = element.clientHeight;
  const k = width / height;
  camera = new THREE.PerspectiveCamera(45, k, 0.1, 1000);
  camera.position.set(0, 10, 15);

  // camera.position.copy(
  //   new THREE.Vector3(10.032411550900214, 15.523325607382143, -27.37892684473546)
  // );
  camera.lookAt(0, 0, 0);
  scene.add(camera);
};

const createRender = () => {
  const element = document.getElementById('container');
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
};

const changeMat = () => {
  console.log(
    cabinets.map((item) => item.name),
    'cabinets'
  );

  cabinets.forEach((obj) => {
    console.log(obj.material.material);
    if (obj.material.map) {
      obj.material = new THREE.MeshBasicMaterial({
        map: new THREE.TextureLoader().load('/dianchi/pack.png'),
        // color: new THREE.Color(0x6D6E6E), // 16进制颜色 - 蓝色
      });
      // 加载新贴图
    } else {
      console.log('没有纹理，纯色');

      obj.material = new THREE.MeshBasicMaterial(obj.olor);
    }
  });
};
const handleDoubleClick = () => {
  if (curCabinet) {
    console.log('双击了机柜:', curCabinet.name);
    emit('customEvent', 'DataView');
  }
};
const selectCabinet = (x, y) => {
  const container = document.getElementById('container');
  const rect = container.getBoundingClientRect(); // 获取容器的边界信息

  const { width, height } = renderer.domElement;
  //射线投射器，可基于鼠标点和相机，在世界坐标系内建立一条射线，用于选中模型
  const raycaster = new THREE.Raycaster();

  // 获取鼠标相对于容器的坐标
  const xcoord = ((x - rect.left) / rect.width) * 2 - 1; // 转换为 -1 到 1 之间
  const ycoord = (-(y - rect.top) / rect.height) * 2 + 1; // 转换为 -1 到 1 之间

  //鼠标在裁剪空间中的点位
  // const pointer = new THREE.Vector2();
  const pointer = new THREE.Vector2(xcoord, ycoord);
  // 鼠标的canvas坐标转裁剪坐标
  // pointer.set((x / width) * 2 - 1, -(y / height) * 2 + 1);
  // 基于鼠标点的裁剪坐标位和相机设置射线投射器
  raycaster.setFromCamera(pointer, camera);
  // 选择机柜
  const intersect = raycaster.intersectObjects(cabinets)[0];
  let intersectObj = intersect ? intersect.object : null;
  let previousMaterial = null; // 保存原始材质
  // 若之前已有机柜被选择，且不等于当前所选择的机柜，取消之前选择的机柜的高亮
  if (curCabinet && curCabinet !== intersectObj) {
    const material = curCabinet.material;
    // console.log(curCabinet.name, material.map.image.src);
    curCabinet.material = material;
    curCabinet.material = new THREE.MeshBasicMaterial({
      map: new THREE.TextureLoader().load('/dianchi/pack.png'),
    });

    // 先解绑，防止重复绑定
    window.removeEventListener('dblclick', handleDoubleClick);
    window.addEventListener('dblclick', handleDoubleClick);
    // material.setValues({
    //   map: new THREE.TextureLoader().load('/dianchi/PlasticMoldGrain001_DISP_4K.png'),
    // });
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
    // console.log(intersectObj, 'intersectObj');

    onMouseMoveCabinet(x, y);
    if (intersectObj !== curCabinet) {
      curCabinet = intersectObj;

      const material = intersectObj.material;
      material.setValues({
        map: new THREE.TextureLoader().load('/model/cabinet-hover.jpg'),
      });
      onMouseOverCabinet(intersectObj);
    } else {
      curCabinet.material = curCabinet.material;
    }
  } else if (curCabinet) {
    curCabinet = null;
    onMouseOutCabinet();
  }
};

const mouseMove = ({ clientX, clientY }) => {
  selectCabinet(clientX, clientY);
};

const onMouseOverCabinet = (cabinet) => {
  // console.log(cabinet.name, cabinet);
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
  const delta = clock.getDelta();
  mixer && mixer.update(delta); // 有动画才更新
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
//获取系统配置信息

const GetSystemConfigurationData = async () => {
  let res = await GetSystemConfiguration();
  console.log(res, '配置信息');

  containerData.value = res.containers;
  store.commit('setCurrentModelName', res.packType.substring(4));
  const modelUrl = res.packType.substring(4) == 52 ? 'single52' : 'single104';
  loadGLTF(modelUrl);

  store.commit('setCurrentContainer', res.containers[0]);
};
//底部左侧总览数据
const GetOverViewDataFun = async () => {
  let res = await GetOverViewData();
  DataStatistics.value = res;

  alarmData.value = res.alertTexts;
};

onMounted(() => {
  GetOverViewDataFun();
  GetSystemConfigurationData();
  // init();
});
onBeforeUnmount(() => {
  disposeScene(scene, renderer);
});
</script>

<style scoped>
#container {
  position: absolute;
  width: 100%;
  height: calc(100% - 0.4rem);
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

.arrowLeft {
  position: absolute;
  top: 2%;
  right: 0.7rem;
  cursor: pointer;
}

.arrowRight {
  position: absolute;
  top: 2%;

  right: 0;
  cursor: pointer;
}
</style>
