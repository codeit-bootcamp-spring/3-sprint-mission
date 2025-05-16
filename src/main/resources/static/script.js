// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!response.ok) throw new Error('Failed to fetch profile');
        const profile = await response.json();

        // Convert base64 encoded bytes to data URL
        return `data:${profile.contentType};base64,${profile.data}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return '/default-avatar.png'; // Fallback to default avatar
    }
}

// Render user list
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = ''; // Clear existing content

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        // Get profile image URL
        const profileUrl = user.profileId ?
            await fetchUserProfile(user.profileId) :
            '/default-avatar.png';

        // Determine badge class and label based on OnlineStatus enum
        let statusClass = '';
        let statusLabel = '';
        switch (user.online) {
            case 'ONLINE':
                statusClass = 'online';
                statusLabel = '온라인';
                break;
            case 'OFFLINE':
                statusClass = 'offline';
                statusLabel = '오프라인';
                break;
            default:
                statusClass = 'offline';
                statusLabel = '알 수 없음';
                break;
        }

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${statusClass}">
                ${statusLabel}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}