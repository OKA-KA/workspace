const setValueHtml = (cell, domainUrl) => {
  // eslint-disable-next-line max-len
  const userImageHtml = `<img src="https://static.cybozu.com/contents/k/image/icon/user/user_16.svg" style="vertical-align:middle;" width="16" height="16"> `;
  let value = '';

  if (cell !== undefined) {
    switch (cell.type) {
      case 'SINGLE_LINE_TEXT':
      case 'RADIO_BUTTON':
      case 'STATUS':
        if (cell.value !== undefined) {
          value = `<span>${cell.value}</span>`;
        }
        break;

      case 'NUMBER':
      case 'RECORD_NUMBER':
        if (cell.value !== undefined) {
          value = `<span>${Number(cell.value).toLocaleString()}</span>`;
        }
        break;

      case 'DATE':
      case 'CREATED_TIME':
      case 'UPDATED_TIME':
        if (Date(cell.value) !== undefined) {
          value = `<span>${luxon.DateTime.fromISO(cell.value).toFormat('yyyy-MM-dd')}</span>`;
        }
        break;

      case 'CREATOR':
      case 'MODIFIER':
        if (cell.value.name !== undefined) {
          // eslint-disable-next-line max-len
          value = `<span><a href="https://${domainUrl}.cybozu.com/k/#/people/user/${cell.value.code}" target="_blank">${userImageHtml} ${cell.value.name}</a></span>`;
        }
        break;

      case 'USER_SELECT':
      case 'STATUS_ASSIGNEE':
        if (cell.value !== undefined) {
          value = '';
          cell.value.forEach((user) => {
            // eslint-disable-next-line max-len
            value += `<span><a href="https://${domainUrl}.cybozu.com/k/#/people/user/${user.code}" target="_blank">${userImageHtml} ${user.name}</a></span>`;
          });
        }
        break;

      default:
        // eslint-disable-next-line no-console
        console.log(`${cell.type}は未対応`);
    }
  }

  return value;
};

const setTableHeadHtml = (columnSettings) => {
  // テンプレート
  const defaultRowHtmlHead = `<tr>`;
  const defaultRowHtmlFoot = `</tr>`;
  const defaultEditColumnHtml = `<th class="recordlist-header-cell-gaia"></th>`;
  const defaultColumnHtml = `<th class="recordlist-header-cell-gaia" style="@style">
  <div class="recordlist-header-cell-inner-wrapper-gaia">
    <div class="recordlist-header-cell-inner-gaia"><span class="recordlist-header-label-gaia">@name</span></div>
  </div>
</th>`;

  // HTMLの組み立て
  let outputHtml = '';
  outputHtml += defaultRowHtmlHead;
  outputHtml += defaultEditColumnHtml;
  columnSettings.forEach((columnSetting) => {
    outputHtml += defaultColumnHtml.replaceAll('@name', columnSetting.name);

    // styleの設定
    let style = '';
    if (columnSetting.visible !== undefined && columnSetting.visible === false) {
      style += `display: none;`;
    }
    if (columnSetting.textAlign !== undefined) {
      style += `text-align: ${columnSetting.textAlign};`;
    } else {
      style += `text-align: left;`;
    }
    outputHtml = outputHtml.replaceAll('@style', style);
  });
  outputHtml += defaultEditColumnHtml;
  outputHtml += defaultRowHtmlFoot;

  return outputHtml;
};

const setTableBodyHtml = (domainUrl, appKey, columnSettings, records) => {
  // テンプレート
  const defaultRowHtmlHead = `<tr class="recordlist-row-gaia @class">`;
  const defaultRowHtmlFoot = `</tr>`;
  const defaultEditColumnHtml = `<td class="recordlist-cell-gaia detail-action-12 recordlist-action-gaia" rowspan=@rowspan style="text-align:center;">
  <a class="recordlist-show-gaia" target="_self" title="レコードの詳細を表示する" href="https://${domainUrl}.cybozu.com/k/${appKey}/show#record=@record">
    <span class="recordlist-detail-gaia"></span>
  </a>
</td>`;
  const defaultRemoveColumnHtml = `<td class="recordlist-cell-gaia recordlist-action-gaia" rowspan=@rowspan>
  <div class="recordlist-cell-edit-and-remove-action">
    <button type="button" class="recordlist-remove-gaia" title="削除する" aria-label="削除する" id="removeButton@record">
      <img class="recordlist-remove-icon-gaia" src="https://static.cybozu.com/contents/k/image/argo/component/recordlist/record-delete.png" alt="">
    </button>
  </div>
</td>`;

  // eslint-disable-next-line max-len
  const defaultColumnHtml = `<td id="@id" class="recordlist-cell-gaia" style="@style" rowspan=@rowspan>
  <div>
    @value
  </div>
</td>`;

  // HTMLの組み立て
  let outputHtml = '';
  let recordCount = 0;
  let rowCount = 0;
  records.forEach((record) => {
    // 変数の設定
    const rowNo = record['レコード番号'].value;
    recordCount += 1;
    rowCount = 1;
    Object.keys(record).forEach((column) => {
      if (record[column].type === 'SUBTABLE') {
        if (rowCount < record[column].value.length) {
          rowCount = record[column].value.length;
        }
      }
    });

    // 行数分処理
    for (let i = 1; i <= rowCount; i += 1) {
      // 先頭の設定
      outputHtml += defaultRowHtmlHead;
      if (i === 1) {
        outputHtml += defaultEditColumnHtml.replaceAll('@record', rowNo).replaceAll('@rowspan', rowCount);
      }

      // 値の設定
      // eslint-disable-next-line no-loop-func
      columnSettings.forEach((columnSetting) => {
        if (columnSetting.isTableColumn !== undefined && columnSetting.isTableColumn === true) {
          // サブテーブルの項目(行数分処理)

          // 項目値の特定
          let cell;
          Object.keys(record).forEach((column) => {
            if (record[column].type === 'SUBTABLE') {
              if (record[column].value[i - 1].value[columnSetting.column] !== undefined) {
                cell = record[column].value[i - 1].value[columnSetting.column];
              }
            }
          });

          // HTMLの書き込み
          outputHtml += defaultColumnHtml
            .replaceAll('@id', columnSetting.column)
            .replaceAll('@value', setValueHtml(cell, domainUrl))
            .replaceAll('@rowspan', 1);

          // HTMLの書き込み
        } else if (i === 1) {
          // サブテーブル以外の項目(初回のみ設定)
          // HTMLの書き込み
          outputHtml += defaultColumnHtml
            .replaceAll('@id', columnSetting.column)
            .replaceAll('@value', setValueHtml(record[columnSetting.column], domainUrl))
            .replaceAll('@rowspan', rowCount);
        }

        // styleの設定
        let style = '';
        if (columnSetting.visible !== undefined && columnSetting.visible === false) {
          style += `display: none;`;
        }
        if (columnSetting.textAlign !== undefined) {
          style += `text-align: ${columnSetting.textAlign};`;
        } else {
          style += `text-align: left;`;
        }
        outputHtml = outputHtml.replaceAll('@style', style);
      });

      // classの設定
      if (recordCount % 2 !== 0) {
        // 奇数行の場合
        outputHtml = outputHtml.replaceAll('@class', 'row-odd');
      } else {
        // 偶数行の場合
        outputHtml = outputHtml.replaceAll('@class', 'row-even');
      }
      // 末尾の設定
      if (i === 1) {
        outputHtml += defaultRemoveColumnHtml.replaceAll('@record', rowNo).replaceAll('@rowspan', rowCount);
      }
      outputHtml += defaultRowHtmlFoot;
    }
  });
  return outputHtml;
};

export default {
  setTableHeadHtml,
  setTableBodyHtml,
};
