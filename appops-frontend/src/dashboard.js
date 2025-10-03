document.addEventListener('DOMContentLoaded', () => {
  // Get userId from URL query params
  let currentUserId = new URLSearchParams(window.location.search).get('userId');
  if (!currentUserId) {
    alert('User ID is required in the URL (e.g., ?userId=74)');
    window.location.href = 'index.html';
    return;
  }

  // Get user email from sessionStorage and compute initials
  const userEmail = sessionStorage.getItem('userEmail') || '';
  let initials = '';
  if (userEmail) {
    const parts = userEmail.split('@')[0].split(/[._]/); // split on . or _
    if (parts.length === 1) {
      initials = parts[0].substring(0, 2).toUpperCase();
    } else {
      initials = parts.map(p => p[0]).join('').substring(0, 2).toUpperCase();
    }
  } else {
    initials = currentUserId.substring(0, 2).toUpperCase();
  }

  // UI setup
  const userNameDisplay = document.getElementById('user-name-display');
  const userInitialsDiv = document.getElementById('user-initials');
  if (userNameDisplay) userNameDisplay.textContent = userEmail || currentUserId;
  if (userInitialsDiv) userInitialsDiv.textContent = initials;

  // Modal logic
  window.openModal = (modalId) => { document.getElementById(modalId).classList.add('show'); };
  window.closeModal = (modalId, event) => {
    if (event && event.target !== document.getElementById(modalId) && !event.target.classList.contains('btn-neutral')) return;
    document.getElementById(modalId).classList.remove('show');
  };
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
      document.querySelectorAll('.modal.show').forEach(modal => modal.classList.remove('show'));
    }
  });

  // Notification
  const showNotification = (message, type = 'success') => {
    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.textContent = message;
    if (type === 'error') notification.classList.add('error');
    document.body.appendChild(notification);
    setTimeout(() => { notification.classList.add('show'); }, 10);
    setTimeout(() => {
      notification.classList.remove('show');
      setTimeout(() => { document.body.removeChild(notification); }, 500);
    }, 3000);
  };

  // Fetch organizations from backend
  async function fetchOrganizations(userId) {
    try {
      const response = await fetch('/DeploymentManager/getAllTenantsOfUser?userId=' + encodeURIComponent(userId));
      if (!response.ok) throw new Error('Failed to fetch organizations');
      return await response.json();
    } catch (e) {
      showNotification('Failed to fetch organizations', 'error');
      return [];
    }
  }

  // Render organizations
  async function renderOrganizations() {
    const orgList = document.getElementById('orgList');
    const organizations = await fetchOrganizations(currentUserId);
    orgList.innerHTML = organizations.length > 0
      ? organizations.map(org => `<div class="org-item" onclick="goToOrgDetails('${encodeURIComponent(org.name)}')"><h4>${org.name}</h4></div>`).join('')
      : `<p>No organizations found. Create one to get started.</p>`;
    populateTenantDropdown(organizations);
  }

  // Create organization
  window.createOrganization = async () => {
    const name = document.getElementById('orgName').value.trim();
    if (!name) { showNotification('Organization name is required.', 'error'); return; }
    // Generate a unique incremental id for each new organization
    let lastOrgId = Number(localStorage.getItem('lastOrgId') || '0');
    const newOrgId = lastOrgId + 1;
    localStorage.setItem('lastOrgId', newOrgId);
    // Build request body for backend: full Tenant object inside 0.value
    const parentId = Math.floor(Math.random() * 99) + 1; // 1 to 99
    const body = {
      0: { value: name },
      1: { value: parentId },
      2: { value: Number(currentUserId) },
      3: { value: true }
    };
    try {
      const response = await fetch('/DeploymentManager/createTenantSimple', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      if (!response.ok) throw new Error('Failed to create organization');
      showNotification(`Organization \"${name}\" created successfully!`);
      document.getElementById('orgName').value = '';
      closeModal('org-modal', { target: document.getElementById('org-modal') });
      renderOrganizations();
    } catch (e) {
      showNotification('Failed to create organization', 'error');
    }
  };

  // Populate tenant dropdown in service modal
  function populateTenantDropdown(organizations = []) {
    const select = document.getElementById('tenantSelection');
    if (!select) return;
    let options = '<option value="">No organizations available</option>';
    if (organizations.length > 0) {
      options = '<option value="__personal__">Use my personal tenant</option>';
      options += organizations.map(org => `<option value="${org.tenantId}">${org.name}</option>`).join('');
    } else {
      options = '<option value="__personal__">Use my personal tenant</option>';
    }
    select.innerHTML = options;
  }

  // Fetch services from backend
  async function fetchServices(userId) {
    try {
      const response = await fetch('/BuilderService/getAllIndesignServicesOfUser?0=' + encodeURIComponent(userId));
      if (!response.ok) throw new Error('Failed to fetch services');
      return await response.json();
    } catch (e) {
      showNotification('Failed to fetch services', 'error');
      return [];
    }
  }

  // Render services
  async function renderServices() {
    const serviceList = document.getElementById('serviceList');
    const services = await fetchServices(currentUserId);
    serviceList.innerHTML = services.length > 0
      ? services.map(srv => `<div class="service-item" onclick="selectItem('Service', '${srv.serviceName}')"><h4>${srv.serviceName}</h4><p style="margin:0;color:var(--text-secondary);font-size:0.9rem;">Version: v1.0.0</p></div>`).join('')
      : `<p>No services found. Create one to get started.</p>`;
  }

  // Create service (implement as needed)
  window.createService = async () => {
    const serviceName = document.getElementById('serviceName').value.trim();
    const tenantSelect = document.getElementById('tenantSelection');
    const tenantId = tenantSelect ? tenantSelect.value : '';
    if (!serviceName) {
      showNotification('Service name is required.', 'error');
      return;
    }
    let userIdToSend = tenantId;
    if (!tenantId || tenantId === '__personal__') {
      userIdToSend = currentUserId;
    }
    const body = {
      0: { value: serviceName },
      1: { value: userIdToSend }
    };
    try {
      const response = await fetch('/BuilderService/createIndesignService', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });
      if (!response.ok) throw new Error('Failed to create service');
      showNotification(`Service \"${serviceName}\" created successfully!`);
      document.getElementById('serviceName').value = '';
      closeModal('service-modal', { target: document.getElementById('service-modal') });
      renderServices();
    } catch (e) {
      showNotification('Failed to create service', 'error');
    }
  };

  // Select item
  window.selectItem = (type, name) => { showNotification(`Selected ${type}: ${name}`); };

  // Redirect to org details page
  window.goToOrgDetails = (orgName) => {
    window.location.href = `org_details.html?orgName=${orgName}`;
  };

  // Initial render
  renderOrganizations();
  renderServices();
});