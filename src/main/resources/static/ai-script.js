const ENDPOINTS = {
    USERS: '/api/user/findAll',
    BINARY_CONTENT: '/api/binaryContent/find?binaryContentId=',
};

async function loadUsers() {
    const response = await fetch(ENDPOINTS.USERS);
    const users = await response.json();

    const container = document.getElementById('user-list');

    for (const user of users) {
        const listItem = document.createElement('li');
        listItem.className = 'user-item';

        const img = document.createElement('img');
        if (user.profileId) {
            try {
                const imgRes = await fetch(ENDPOINTS.BINARY_CONTENT + user.profileId);
                const imgData = await imgRes.json();
                const base64 = `data:${imgData.contentType};base64,${imgData.bytes}`;
                img.src = base64;
            } catch {
                img.src = '/default.png';
            }
        } else {
            img.src = '/default.png';
        }

        const info = document.createElement('div');
        info.className = 'user-info';
        info.innerHTML = `
      <div class="username">${user.username}</div>
      <div class="email">${user.email}</div>
    `;

        const badge = document.createElement('div');
        badge.className = 'badge';
        badge.innerText = user.online === true ? '온라인' : '오프라인';

        listItem.appendChild(img);
        listItem.appendChild(info);
        listItem.appendChild(badge);

        container.appendChild(listItem);
    }
}

window.addEventListener('DOMContentLoaded', loadUsers);