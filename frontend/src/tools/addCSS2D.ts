import { CSS2DRenderer, CSS2DObject } from 'three/examples/jsm/renderers/CSS2DRenderer';

// 用于添加CSS2D标签的函数
export const addCSS2D = (scene: any, camera: any, textLable: string) => {
  const div = document.createElement('div');
  div.className = 'CSS2D';
  div.innerHTML = `${textLable}`;
  div.style.color = 'white';
  div.style.position = 'absolute';
  div.style.fontSize = '0.08rem';

  div.style.top = '0';
  div.style.left = '0';

  div.style.backgroundColor = 'rgba(0, 0, 0, 0.5)';
  div.style.padding = '20px';
  div.style.borderRadius = '4px';
  div.style.textAlign = 'center';

  // 设置背景图片
  // div.style.backgroundImage = `url(${imageUrl})`;
  // div.style.backgroundSize = 'cover'; // 背景图片覆盖整个标签
  // div.style.backgroundPosition = 'center'; // 图片居中

  const label = new CSS2DObject(div);
  label.position.set(0, 5, 0); // 稍微上移，避免和面重叠
  scene.add(label);

  // 创建CSS2DRenderer并附加到页面
  const css2DRenderer = new CSS2DRenderer();
  const element = document.getElementById('containerLoop');
  css2DRenderer.setSize(element.clientWidth, element.clientHeight);

  //设置2d渲染器布局
  css2DRenderer.domElement.style.position = 'absolute';
  css2DRenderer.domElement.style.top = '0px';
  css2DRenderer.domElement.style.left = '0px';
  css2DRenderer.domElement.style.pointerEvents = 'none'; // 避免阻挡鼠标事件

  element?.appendChild(css2DRenderer.domElement);
  // 渲染函数
  const animate = () => {
    requestAnimationFrame(animate);
    css2DRenderer.render(scene, camera);
  };
  animate();
  return label;
};
