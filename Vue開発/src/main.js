// eslint-disable-next-line import/no-extraneous-dependencies
import Vue from 'vue';
import ExtApp from './vue/ExtApp.vue';
import set from './js/Setting';
import dch from './js/DataControlHelper';

Vue.config.productionTip = false;

const getRecords = () => {
  // データの取得
  const koutsuhiBody = {
    app: set.APP_KEY,
    query: 'order by レコード番号',
  };
  const dt = dch.execute('GET', set.DOMAIN_URL, set.API_TOKEN, koutsuhiBody);

  return dt.records;
};

// 外部アプリケーション用表示の読み込み
new Vue({
  render: (h) => h(ExtApp, { props: { record: getRecords() } }),
}).$mount('#app');
