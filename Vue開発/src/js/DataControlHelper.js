const execute = (pHttpMethod, pDomainUrl, pApiToken, pBody) => {
  let url = `https://${pDomainUrl}.cybozu.com/k/v1/records.json?app=${pBody.app}`;
  if (pBody.query !== undefined) {
    url += `&query=${encodeURIComponent(pBody.query)}`;
  }

  if (pBody.fields !== undefined) {
    url += `&fields=${encodeURIComponent(pBody.fields)}`;
  }

  if (pBody.ids !== undefined) {
    for (let i = 0; i < pBody.ids.length; i += 1) {
      url += `&ids[${i}]=${encodeURIComponent(pBody.ids[i])}`;
    }
  }

  const xhr = new XMLHttpRequest();
  xhr.open(pHttpMethod, url, false);
  xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
  xhr.setRequestHeader('X-Cybozu-API-Token', `${pApiToken}`);

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

export default {
  execute,
};
