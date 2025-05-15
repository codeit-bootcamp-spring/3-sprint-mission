const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

const userList = document.getElementById("user-list");

async function fetchUsers() {
    const res = await fetch(ENDPOINTS.USERS);
    if (!res.ok) {
        console.error("사용자 목록 요청 실패");
        return [];
    }
    return res.json();
}

async function fetchUserImageBase64(profileId) {
    const res = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
    if (!res.ok) {
        console.warn(`이미지 요청 실패: ${profileId}`);
        return null;
    }
    const data = await res.json();
    return `data:${data.contentType};base64,${data.bytes}`;
}

async function renderUsers() {
    const users = await fetchUsers();
    for (const user of users) {
        const item = document.createElement("div");
        item.className = "user-item";

        let imageUrl = "/default-profile.png"; // 기본 이미지
        if (user.profileId) {
            const base64Image = await fetchUserImageBase64(user.profileId);
            if (base64Image) {
                imageUrl = base64Image;
            }
        }

        item.innerHTML = `
            <img class="user-image" src="${imageUrl}" alt="${user.username}">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userList.appendChild(item);
    }
}

renderUsers();
