
<template>
  <div id="app">
    <div class="koutsuhiTable">
      <ModalWindowOkNg
        ref="mw"
        title="レコード削除"
        :message="modalMessage"
        @OkClose="closeModal"
      />
      <table class="recordlist-gaia">
        <thead v-html="thead" />
        <tbody v-html="tbody" />
      </table>
    </div>
  </div>
</template>

<script>
import set from '../js/Setting';
import tch from '../js/TableCreateHelper';
import dch from '../js/DataControlHelper';
import ModalWindowOkNg from './ModalWindowOkNg.vue';

// データの設定
const columnSettings = [
  {
    column: 'レコード番号',
    name: 'No',
    textAlign: 'right',
  }, {
    column: 'TF_KTSSDT',
    name: '申請日',
    textAlign: 'center',
  }, {
    column: 'TF_KTSSNM',
    name: '申請者',
  }, {
    column: 'TF_KTSNS1',
    name: '承認者1',
  }, {
    column: 'TF_KTSNS2',
    name: '承認者2',
  }, {
    column: 'TF_KTKTDT',
    name: '日付',
    isTableColumn: true,
    textAlign: 'center',
  }, {
    column: 'TF_KTJSTT',
    name: '乗車地点',
    isTableColumn: true,
  }, {
    column: 'TF_KTKSTT',
    name: '降車地点',
    isTableColumn: true,
  }, {
    column: 'TF_KTKTOU',
    name: '片道/往復',
    isTableColumn: true,
  }, {
    column: 'TF_KTKNGK',
    name: '金額',
    isTableColumn: true,
    textAlign: 'right',
  }, {
    column: 'TF_KTGKKG',
    name: '合計金額',
    isTableColumn: true,
    textAlign: 'right',
  }, {
    column: 'TF_KTKNTL',
    name: '合計金額',
    isTableColumn: false,
    textAlign: 'right',
    visible: false,
  }, {
    column: 'TF_KTGKTL',
    name: '合計金額',
    isTableColumn: false,
    textAlign: 'right',
  }, {
    column: 'TF_KTIKSK',
    name: '行先',
    isTableColumn: true,
  }, {
    column: 'TF_KTYUKN',
    name: '用件',
    isTableColumn: true,
  }, {
    column: 'ステータス',
    name: 'ステータス',
  }, {
    column: '作業者',
    name: '作業者',
  }, {
    column: '更新者',
    name: '更新者',
    visible: false,
  }, {
    column: '更新日時',
    name: '更新日時',
    textAlign: 'center',
    visible: false,
  },
];

export default {
  components: {
    ModalWindowOkNg,
  },
  props: {
    record: {
      type: Object,
      default: undefined,
    },
  },
  data() {
    return {
      thead: tch.setTableHeadHtml(columnSettings),
      tbody: tch.setTableBodyHtml(set.DOMAIN_URL, set.APP_KEY, columnSettings, this.record),
      currentRecord: 0,
    };
  },
  computed: {
    modalMessage() {
      return `以下のレコードを削除します。\nよろしいですか？\n\nNo：${this.currentRecord}`;
    },
  },
  mounted() {
    const self = this;
    /* ボタンイベント(削除)************************************* */
    $(document).on('click', '.recordlist-remove-gaia', (e) => {
      self.currentRecord = Number(e.currentTarget.id.replaceAll('removeButton', ''));
      self.openModal();
    });
  },
  methods: {
    openModal() {
      this.$refs.mw.openModal();
    },
    closeModal() {
      // データの削除
      const deleteBody = {
        app: set.APP_KEY,
        ids: [this.currentRecord],
      };
      dch.execute('DELETE', set.DOMAIN_URL, set.API_TOKEN, deleteBody);

      // 画面のリロード
      window.location.reload();
    },
  },
};
</script>

<style>
.koutsuhiTable .row-odd {
  background-color: #e9f3fb !important;
}
.koutsuhiTable .row-even {
  background-color: #fff !important;
}
</style>
