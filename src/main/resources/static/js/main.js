var queryString = window.location.search;
var urlParams = new URLSearchParams(queryString);
var id = urlParams.get('id');
window.addEventListener('beforeunload', function (e) {
  console.log('UNLOAD');
  var requestBody = {
    sessionId: id,
  };

  fetch('http://localhost:8080/chat/clean', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
    .then((response) => {})
    .then((data) => {})
    .catch((error) => {});
});
function deleteSession(id) {
  console.log('Delete session');
  var requestBody = {
    sessionId: id,
  };

  fetch('http://localhost:8080/chat/delete-session', {
    method: 'POST',
    redirect: 'follow',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
    .then((response) => {
      console.log(response.data);
      if (response.redirected) {
        window.location.href = response.url;
      }
      //window.location.reload(true);
    })
    .then((data) => {})
    .catch((error) => {});
}
function saveSession(saveId) {
  console.log('Save session');
  var requestBody = {
    sessionId: saveId,
  };

  fetch('http://localhost:8080/chat/save-session', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
    .then((response) => {
      console.log(response.data);
    })
    .then((data) => {})
    .catch((error) => {});
}
function newSession(id) {
  console.log('New session');
  var requestBody = {
    sessionId: id,
  };

  fetch('http://localhost:8080/chat/new-session', {
    method: 'POST',
    redirect: 'follow',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
    .then((response) => {
      console.log(response.data);
      if (response.redirected) {
        window.location.href = response.url;
      }
    })
    .then((data) => {})
    .catch((error) => {});
}
function changeSession(id) {
  console.log('Changing session');
  var requestBody = {
    sessionId: id,
  };

  fetch('http://localhost:8080/chat/change-session', {
    method: 'POST',
    redirect: 'follow',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestBody),
  })
    .then((response) => {
      console.log(response.data);
      if (response.redirected) {
        window.location.href = response.url;
      }
    })
    .then((data) => {})
    .catch((error) => {});
}

const copyButton = document.getElementById('copy-button');

copyButton.addEventListener('click', function () {
  const text = document.getElementById('text-to-copy');
  const wrapper = document.createElement('div');
  wrapper.appendChild(text.cloneNode(true));

  navigator.clipboard
    .writeText(text.innerHTML) //wrapper.innerHTML
    .then(function () {
      console.log('Text copied to clipboard:', text);
      // You can add your own success message or visual feedback here
    })
    .catch(function (error) {
      console.error('Failed to copy text:', error);
      // You can add your own error message or visual feedback here
    });
});

// Show the initial state of the elements
