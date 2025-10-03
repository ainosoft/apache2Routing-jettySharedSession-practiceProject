// All the functions you cut from login.html go here.
// Make sure to attach them to the window object so the inline onclick handlers can find them.

// Update: Accept userId and redirect with ?userId=...
window.handleSuccessfulRedirect = function (email, name, userId) {
    console.log(`Authentication successful for ${name} (${email}, id: ${userId}). Redirecting...`);
    sessionStorage.setItem('userEmail', email);
    sessionStorage.setItem('userName', name);
    // Redirect to dashboard with userId in URL
    window.location.href = 'appops_dashboard.html?userId=' + encodeURIComponent(userId);
}

async function fetchUserInfo(accessToken) {
    const response = await fetch('https://www.googleapis.com/oauth2/v2/userinfo', {
        headers: { Authorization: `Bearer ${accessToken}` }
    });
    if (!response.ok) throw new Error('Failed to fetch user info from Google.');
    return response.json();
}

function handleGoogleSignIn() {
    if (typeof google === 'undefined') {
        alert('Google Identity Services not loaded. Please wait and try again.');
        return;
    }

    const client = google.accounts.oauth2.initTokenClient({
        client_id: '490602105831-b5ptlvb0rm26h3fr70hrmobrui7n1rjk.apps.googleusercontent.com',
        scope: 'https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile',
        prompt: '',
        callback: async (tokenResponse) => {
            const { access_token } = tokenResponse;
            if (!access_token) return;

            try {
                const userInfo = await fetchUserInfo(access_token);
                const { email, name } = userInfo;

                const authSlim = {
                    principle: email,
                    accountType: 'OAUTH',
                    oAuthProvider: 'GoogleOAuthWeb',
                    validator: access_token,
                    oAuthToken: access_token,
                };

                const data = { 0: { value: authSlim } };
               
                const url = '/UserService/signInUser';

                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-Ao-Tenant-Id': '1'
                    },
                    body: JSON.stringify(data),
                });

                if (!response.ok) {
                    throw new Error(`Backend request failed with status: ${response.status}`);
                }

                const responseText = await response.text();
                // TODO: Get userId from backend response (replace 74 with actual userId)
                if (responseText.trim() === 'true') {
                    // You must get the userId from your backend response here!
                  
                    handleSuccessfulRedirect(email, name, 74);
                } else {
                    throw new Error('Backend authentication failed.');
                }
            } catch (error) {
                console.error('Sign-in process failed:', error);
                alert(`An error occurred during sign-in: ${error.message}`);
            }
        },
    });
    client.requestAccessToken();
}

window.toggleForms = function () {
    const signinForm = document.getElementById('signin-form');
    const signupForm = document.getElementById('signup-form');
    signinForm.style.display = (signinForm.style.display === 'none') ? 'block' : 'none';
    signupForm.style.display = (signupForm.style.display === 'none') ? 'block' : 'none';
}

window.handleSignIn = function () { alert('Manual sign-in is not implemented.'); }
window.handleSignUp = function () { alert('Manual sign-up is not implemented.'); }

// Add event listeners after the DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
    // Sign-in form submission
    const signinFormElement = document.getElementById('signin-form-element');
    if (signinFormElement) {
        signinFormElement.addEventListener('submit', (event) => {
            event.preventDefault();
            window.handleSignIn();
        });
    }

    // Google Sign-In button
    const googleSignInBtn = document.getElementById('google-signin-btn');
    if (googleSignInBtn) {
        googleSignInBtn.addEventListener('click', handleGoogleSignIn);
    }

    // "Sign up" link to toggle forms
    const signupLink = document.getElementById('signup-link');
    if (signupLink) {
        signupLink.addEventListener('click', window.toggleForms);
    }

    // Sign-up form submission
    const signupFormElement = document.getElementById('signup-form-element');
    if (signupFormElement) {
        signupFormElement.addEventListener('submit', (event) => {
            event.preventDefault();
            window.handleSignUp();
        });
    }

    // "Sign in" link to toggle forms
    const signinLink = document.getElementById('signin-link');
    if (signinLink) {
        signinLink.addEventListener('click', window.toggleForms);
    }
});