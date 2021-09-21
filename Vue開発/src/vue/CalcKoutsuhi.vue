<script>
import set from '../js/Setting';
import kah from '../js/KoutsuhiApiHelper';

export default {
  mounted() {
    const fromClass = 'value-5758354';
    const toClass = 'value-5758355';

    /* ボタンイベント(交通費計算)************************************* */
    $(document).on('click', '.calc-expansion-row-image', (e) => {
      // 押下したレコード情報を取得
      const currentRow = e.currentTarget.id.replaceAll('calcButton', '');
      const jousya = $(`.expansion-row-${currentRow} .${fromClass} input`)[0].value;
      const kousya = $(`.expansion-row-${currentRow} .${toClass} input`)[0].value;

      const apiResult = kah.getKoutsuhiUrl(set.KOUTSUHI_API_KEY, jousya, kousya);
      if (apiResult !== undefined && apiResult.ResultSet !== undefined) {
        if (apiResult.ResultSet.Error === undefined) {
          window.open(apiResult.ResultSet.ResourceURI, '_blank');
        } else {
          alert(apiResult.ResultSet.Error.Message);
        }
      }
    });

    /* 入力イベント(乗車,降車)************************************* */
    $(document).on('input focus', `.${fromClass},.${toClass}`, (e) => {
      // 入力補助機能の付加
      const apiResult = kah.getStationInfo(set.KOUTSUHI_API_KEY, e.target.value);
      const koutsuhiListData = [];
      if (apiResult !== undefined && apiResult.ResultSet !== undefined && apiResult.ResultSet.Point !== undefined) {
        if (apiResult.ResultSet.Point.Station !== undefined) {
          koutsuhiListData.push({
            label: `${apiResult.ResultSet.Point.Station.Name}`,
            value: apiResult.ResultSet.Point.Station.Name,
          });
        } else {
          apiResult.ResultSet.Point.forEach((ar) => {
            koutsuhiListData.push({
              label: `${ar.Station.Name}`,
              value: ar.Station.Name,
            });
          });
        }
      }
      $(e.target).autocomplete({
        source: koutsuhiListData,
        autoFocus: true,
      });
    });
  },
};
</script>

<style>
#clac-koutsuhi .content {
  padding: 20px;
  width: 50%;
}

#clac-koutsuhi .control-label-gaia {
  background-color: transparent;
}

#clac-koutsuhi .input-row .gaia-ui-actionmenu-save {
  margin-top: 25px;
}

#clac-koutsuhi .button-row {
  margin-top: 60px;
}

#clac-koutsuhi .gaia-ui-actionmenu-save {
  margin-left: 8px;
}

/* 各リスト項目のパディング設定 */
.ui-menu .ui-menu-item-wrapper {
  position: relative;
  padding: 6px 1em 6px 0.4em;
}

/* リスト全体の背景 */
.ui-widget-content {
  border: 1px solid #c5c5c5 !important;
  background: #f7f9fa !important;
  color: #333 !important;
}
/* リスト hover 時のカラー */
.ui-state-active {
  background: #d8e1e6 !important;
}
</style>
