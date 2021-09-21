const getStationInfo = (pApiKey, pStationName) => {
  // 入力無しの場合はそのまま処理を抜ける
  if (pStationName === undefined || pStationName === '') {
    return undefined;
  }

  let url = `https://api.ekispert.jp/v1/json/station`;

  url += `?key=${encodeURIComponent(pApiKey)}`;
  url += `&name=${encodeURIComponent(pStationName)}`;
  url += `&limit=${encodeURIComponent(10)}`;

  const xhr = new XMLHttpRequest();
  xhr.open('GET', url, false);
  xhr.setRequestHeader('Accept', 'application/json');

  let result;
  xhr.onload = () => {
    if (xhr.status === 200) {
      // success
      result = JSON.parse(xhr.responseText);
    } else {
      // error
      // eslint-disable-next-line no-console
      console.log(JSON.parse(xhr.responseText));
    }
  };
  xhr.send();

  return result;
};

const getKoutsuhiUrl = (pApiKey, pFromStationName, pToStationName) => {
  // いずれかが無しの場合はそのまま処理を抜ける
  if (pFromStationName === undefined || pFromStationName === '' || pToStationName === undefined || pToStationName === '') {
    return undefined;
  }

  let url = `https://api.ekispert.jp/v1/json/search/course/light`;

  url += `?key=${encodeURIComponent(pApiKey)}`;
  url += `&from=${encodeURIComponent(pFromStationName)}`;
  url += `&to=${encodeURIComponent(pToStationName)}`;

  const xhr = new XMLHttpRequest();
  xhr.open('GET', url, false);
  xhr.setRequestHeader('Accept', 'application/json');

  let result;
  xhr.onload = () => {
    if (xhr.status === 200) {
      // success
      result = JSON.parse(xhr.responseText);
    } else {
      // error
      // eslint-disable-next-line no-console
      console.log(JSON.parse(xhr.responseText));
      result = JSON.parse(xhr.responseText);
    }
  };
  xhr.send();

  return result;
};

export default {
  getStationInfo,
  getKoutsuhiUrl,
};
