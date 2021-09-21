// eslint-disable-next-line import/no-extraneous-dependencies
import Vue from 'vue';
import TableKoutsuhi from './vue/TableKoutsuhi.vue';
import CalcKoutsuhi from './vue/CalcKoutsuhi.vue';

Vue.config.productionTip = false;

// 一覧画面の埋込
kintone.events.on(['app.record.index.show'], (event) => {
  new Vue({
    render: (h) => h(TableKoutsuhi, { props: { record: event.records } }),
  }).$mount('#app');
  return event;
});

// 交通費取得ボタンの埋込
kintone.events.on(['app.record.create.show', 'app.record.edit.show', 'app.record.index.edit.show'], (event) => {
  const app = new Vue({
    render: (h) => h(CalcKoutsuhi),
  }).$mount();
  $('#record-gaia').append(app.$el);
  return event;
});
