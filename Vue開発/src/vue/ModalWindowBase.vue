<template>
  <div>
    <div
      v-show="showContent"
      class="modalWindow-custom"
    >
      <div
        class="content"
        style="white-space: pre-wrap; word-wrap: break-word"
      >
        <slot name="window">
          親コンポーネントから埋め込むコンテンツ
        </slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    showContent: { type: Boolean, required: true },
  },
};
</script>

<style>
.modalWindow-custom {
  /*要素を重ねた時の順番*/
  z-index: 1;

  /*画面全体を覆う設定*/
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);

  /*画面の中央に要素を表示させる設定*/
  display: flex;
  align-items: center;
  justify-content: center;
}

.modalWindow-custom .content {
  z-index: 2;
  width: 30%;
  background: #fff;
}
</style>
