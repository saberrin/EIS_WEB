import * as THREE from 'three';

/* 顶点着色器 */
const vertexs = `varying vec2 vUv;
void main() {
  vec4 mvPosition = modelViewMatrix * vec4( position, 1.0 );
  vUv = uv;
  gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1.0);
}`;

/* 片元着色器 */
const fragments = `
uniform float time;
uniform float opacity;
uniform vec3 color;
uniform float num;
uniform float hiz;
varying vec2 vUv;

void main() {
  // 计算边缘暗化效果，使四个边都变暗
float edgeX = smoothstep(0.0, 15.3, max(vUv.x, 1.0 - vUv.x)); // 增大阈值范围
float edgeY = smoothstep(0.0, 15.3, max(vUv.y, 1.0 - vUv.y)); // 同样增大阈值范围
   float edgeFactor = 1.0 - (edgeX * edgeY); // 反转，使边缘变亮


  vec3 brightColor = min(color * 1.5, vec3(1.0)); // 提高亮度但不超1.0
  vec3 finalColor = mix(color, brightColor, edgeFactor); // 颜色过渡

  
  // 计算动态流动效果（保留你原来的逻辑）
  vec4 fragColor = vec4(0.);
  float sinWave = sin((vUv.y - time * hiz) * 1. * num);
  float high = 0.92;
  float medium = 0.4;
  
  if (sinWave > high) {
    fragColor = vec4(mix(vec3(.8, 1., 1.), finalColor, (1. - sinWave) / (1. - high)), 1.);
  } else if (sinWave > medium) {
    fragColor = vec4(finalColor, mix(1., 0., 1. - (sinWave - medium) / (high - medium)));
  } else {
    fragColor = vec4(finalColor, 0.);
  }

  // 额外的渐变效果
  vec3 fade = mix(finalColor, vec3(0., 0., 0.), vUv.y);
  fragColor = mix(fragColor, vec4(fade, 1.), 0.85);

  // 透明度控制，确保顶部和底部有良好渐变
  gl_FragColor = vec4(fragColor.rgb, fragColor.a * opacity * (1. - vUv.y));

}`;

// 自定义材质
const material = new THREE.ShaderMaterial({
  uniforms: {
    time: {
      type: 'pv2',
      value: 0,
    },
    color: {
      type: 'uvs',
      value: new THREE.Color('#799FE8'),
    },
    opacity: {
      type: 'pv2',
      value: 1.0,
    },
    num: {
      type: 'pv2',
      value: 10,
    },
    hiz: {
      type: 'pv2',
      value: 0.03,
    },
  },
  vertexShader: vertexs,
  fragmentShader: fragments,
  blending: THREE.AdditiveBlending,
  transparent: !0,
  depthWrite: !1,
  depthTest: !0,
  side: THREE.DoubleSide,
});

// 新增顶部的三角形索引
let topArr = [];
let posArr = [];

let uvrr = [];
let x_min = -1,
  y_min = -3,
  x_max = 6,
  y_max = 3;
// let c = [-10, -10, 10, 0, 10, 10, 0, 10, -10, -10];
let h = 5; //围墙拉伸高度

let c = [
  x_min,
  y_min,
  x_max,
  y_min,
  x_max,
  y_max,
  x_min,
  y_max,
  x_min,
  y_min, // 重新闭合形成封闭区域
];
// 这里我们用c数组来创建顶部
for (let i = 0; i < c.length - 4; i += 2) {
  if (i + 6 >= c.length) break; // 避免越界
  // 顶面三角形1
  topArr.push(c[i], c[i + 1], h, c[i + 2], c[i + 3], h, c[i + 4], c[i + 5], h);

  // 顶面三角形2
  topArr.push(c[i], c[i + 1], h, c[i + 4], c[i + 5], h, c[i + 6], c[i + 7], h);

  // UV 贴图坐标
  uvrr.push(0, 0, 1, 0, 1, 1);
  uvrr.push(0, 0, 1, 1, 0, 1);
}

for (let i = 0; i < c.length - 2; i += 2) {
  if (i + 2 >= c.length) break; // 防止访问超出范围
  // 矩形的三角形1
  posArr.push(c[i], c[i + 1], 0, c[i + 2], c[i + 3], 0, c[i + 2], c[i + 3], h);

  // 矩形的三角形2
  posArr.push(c[i], c[i + 1], 0, c[i + 2], c[i + 3], h, c[i], c[i + 1], h);

  // 注意顺序问题，和顶点位置坐标对应
  uvrr.push(0, 0, 1, 0, 1, 1);

  uvrr.push(0, 0, 1, 1, 0, 1);
}

// 合并顶部
posArr = posArr.concat(topArr);

const geometry = new THREE.BufferGeometry(); //声明一个空几何体对象

// 设置几何体attributes属性的位置position属性
geometry.attributes.position = new THREE.BufferAttribute(new Float32Array(posArr), 3);

// 设置几何体attributes属性的位置uv属性
geometry.attributes.uv = new THREE.BufferAttribute(new Float32Array(uvrr), 2);

geometry.computeVertexNormals();

const mesh = new THREE.Mesh(geometry, material); //网格模型对象Mesh
mesh.rotation.x = -Math.PI / 2;
function animate() {
  requestAnimationFrame(animate);

  // material.uniforms.time.value += 0.1;
}
export const addFence = (scene: any) => {
  scene.add(mesh);
  animate();
};
