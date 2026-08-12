// DOM Elements
const usernameInput = document.getElementById('usernameInput');
const searchBtn = document.getElementById('searchBtn');
const themeToggleBtn = document.getElementById('themeToggleBtn');

const loadingState = document.getElementById('loadingState');
const errorState = document.getElementById('errorState');
const errorMessage = document.getElementById('errorMessage');
const profileCard = document.getElementById('profileCard');

// Profile Elements
const avatar = document.getElementById('avatar');
const nameElement = document.getElementById('name');
const usernameLink = document.getElementById('usernameLink');
const joinedDate = document.getElementById('joinedDate');
const bioElement = document.getElementById('bio');

const publicRepos = document.getElementById('publicRepos');
const followers = document.getElementById('followers');
const following = document.getElementById('following');

const locationText = document.getElementById('locationText');
const blogLink = document.getElementById('blogLink');
const twitterText = document.getElementById('twitterText');
const companyText = document.getElementById('companyText');
const reposList = document.getElementById('reposList');
const profileBtn = document.getElementById('profileBtn');

// Initialize App
document.addEventListener('DOMContentLoaded', () => {
    // Check saved theme preference
    const savedTheme = localStorage.getItem('theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    // Fetch default user profile on initial load
    fetchGitHubProfile('octocat');
});

// Event Listeners
searchBtn.addEventListener('click', handleSearch);

usernameInput.addEventListener('keypress', (event) => {
    if (event.key === 'Enter') {
        handleSearch();
    }
});

themeToggleBtn.addEventListener('click', toggleTheme);

// Handle Search Trigger
function handleSearch() {
    const query = usernameInput.value.trim();
    if (query) {
        fetchGitHubProfile(query);
    }
}

// Main Async Function to Fetch Profile Data using GitHub REST API
async function fetchGitHubProfile(username) {
    showLoading();

    try {
        // Fetch User Info
        const response = await fetch(`https://api.github.com/users/${username}`);
        
        if (!response.ok) {
            if (response.status === 404) {
                showError('User Not Found', 'The requested GitHub username does not exist.');
            } else if (response.status === 403) {
                showError('Rate Limit Exceeded', 'GitHub API rate limit exceeded. Please try again later.');
            } else {
                showError('Error', `Failed to retrieve user profile (Status: ${response.status})`);
            }
            return;
        }

        const data = await response.json();

        // Fetch User Public Repositories (Top 4 recently updated)
        let reposData = [];
        try {
            const reposResponse = await fetch(`https://api.github.com/users/${username}/repos?sort=updated&per_page=4`);
            if (reposResponse.ok) {
                reposData = await reposResponse.json();
            }
        } catch (repoErr) {
            console.warn('Failed to fetch repositories:', repoErr);
        }

        // Render Data into UI
        displayProfile(data, reposData);
    } catch (error) {
        console.error('Fetch Error:', error);
        showError('Network Error', 'Please check your Internet connection and try again.');
    }
}

// Display Profile Data in DOM
function displayProfile(user, repos) {
    // Avatar & Name
    avatar.src = user.avatar_url;
    avatar.alt = `${user.login}'s profile picture`;
    nameElement.textContent = user.name || user.login;
    usernameLink.textContent = `@${user.login}`;
    usernameLink.href = user.html_url;

    // Joined Date Formatting
    const dateOptions = { day: 'numeric', month: 'short', year: 'numeric' };
    const dateObj = new Date(user.created_at);
    joinedDate.textContent = dateObj.toLocaleDateString('en-US', dateOptions);

    // Bio
    if (user.bio) {
        bioElement.textContent = user.bio;
        bioElement.classList.remove('not-available');
    } else {
        bioElement.textContent = 'This profile has no bio.';
        bioElement.classList.add('not-available');
    }

    // Stats
    publicRepos.textContent = user.public_repos.toLocaleString();
    followers.textContent = user.followers.toLocaleString();
    following.textContent = user.following.toLocaleString();

    // Meta Details
    updateMetaItem('metaLocation', locationText, user.location);
    
    // Blog / Website
    if (user.blog) {
        let formattedBlog = user.blog.startsWith('http') ? user.blog : `https://${user.blog}`;
        blogLink.textContent = user.blog;
        blogLink.href = formattedBlog;
        document.getElementById('metaBlog').classList.remove('disabled');
    } else {
        blogLink.textContent = 'Not Available';
        blogLink.removeAttribute('href');
        document.getElementById('metaBlog').classList.add('disabled');
    }

    // Twitter
    if (user.twitter_username) {
        twitterText.textContent = `@${user.twitter_username}`;
        document.getElementById('metaTwitter').classList.remove('disabled');
    } else {
        twitterText.textContent = 'Not Available';
        document.getElementById('metaTwitter').classList.add('disabled');
    }

    // Company
    updateMetaItem('metaCompany', companyText, user.company);

    // External GitHub Profile Button
    profileBtn.href = user.html_url;

    // Render Recent Repositories
    renderRepositories(repos);

    // Show Profile Card
    showProfile();
}

// Helper to update metadata fields
function updateMetaItem(containerId, textElement, value) {
    const container = document.getElementById(containerId);
    if (value) {
        textElement.textContent = value;
        container.classList.remove('disabled');
    } else {
        textElement.textContent = 'Not Available';
        container.classList.add('disabled');
    }
}

// Render Recent Repositories
function renderRepositories(repos) {
    reposList.innerHTML = '';

    if (!repos || repos.length === 0) {
        reposList.innerHTML = '<p style="color: var(--text-muted); font-size: 0.85rem; grid-column: span 2;">No public repositories found.</p>';
        return;
    }

    repos.forEach(repo => {
        const repoCard = document.createElement('a');
        repoCard.className = 'repo-card';
        repoCard.href = repo.html_url;
        repoCard.target = '_blank';
        repoCard.rel = 'noopener noreferrer';

        const repoLanguage = repo.language || 'Plain Text';
        const stars = repo.stargazers_count || 0;

        repoCard.innerHTML = `
            <div class="repo-name">${repo.name}</div>
            <div class="repo-meta">
                <span class="repo-lang">
                    <span class="lang-dot"></span>
                    ${repoLanguage}
                </span>
                <span class="repo-stars">
                    <i class="fa-regular fa-star"></i> ${stars}
                </span>
            </div>
        `;

        reposList.appendChild(repoCard);
    });
}

// UI State Management
function showLoading() {
    loadingState.classList.remove('hidden');
    errorState.classList.add('hidden');
    profileCard.classList.add('hidden');
}

function showError(title, message) {
    loadingState.classList.add('hidden');
    profileCard.classList.add('hidden');
    errorMessage.textContent = title;
    document.querySelector('#errorState p').textContent = message;
    errorState.classList.remove('hidden');
}

function showProfile() {
    loadingState.classList.add('hidden');
    errorState.classList.add('hidden');
    profileCard.classList.remove('hidden');
}

// Theme Toggle Handler
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'light' ? 'dark' : 'light';
    
    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);
    updateThemeIcon(newTheme);
}

function updateThemeIcon(theme) {
    const icon = themeToggleBtn.querySelector('i');
    if (theme === 'light') {
        icon.className = 'fa-solid fa-sun';
    } else {
        icon.className = 'fa-solid fa-moon';
    }
}
