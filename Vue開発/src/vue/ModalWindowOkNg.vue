<template>
  <ModalWindowBase :show-content="showContent">
    <template #window>
      <div class="title">
        {{ title }}
      </div>
      <p class="message">
        {{ message }}
      </p>
      <p>
        <button
          type="button"
          class="gaia-ui-actionmenu-save"
          style="user-select: none;"
          @click="closeModalOk"
        >
          {{ ok }}
        </button>
        <button
          type="button"
          class="gaia-ui-actionmenu-cancel"
          style="user-select: none;"
          @click="closeModalNg"
        >
          {{ ng }}
        </button>
      </p>
    </template>
  </ModalWindowBase>
</template>

<script>
import ModalWindowBase from './ModalWindowBase.vue';

export default {
  components: {
    ModalWindowBase,
  },
  props: {
    title: { type: String, required: true },
    message: { type: String, required: true },
    ok: { type: String, required: true, default: 'OK' },
    ng: { type: String, required: true, default: 'キャンセル' },
  },
  data() {
    return { showContent: false, isOk: false };
  },
  methods: {
    openModal() {
      this.showContent = true;
      this.isOk = false;
    },
    closeModalOk() {
      this.showContent = false;
      this.isOk = true;
      this.$emit('OkClose');
    },
    closeModalNg() {
      this.showContent = false;
      this.isOk = false;
    },
  },
};
</script>

<style>
.modalWindow-custom .content .title {
  padding: 1em;
  background: #448aca;
  color: white;
  font-weight: 700;
}

.modalWindow-custom .content .message {
  padding: 1em;
  background: #fff;
  font-weight: 700;
}

.modalWindow-custom .content .gaia-ui-actionmenu-cancel {
  margin-left: 17px;
}
</style>
