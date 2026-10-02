// Campus Lost & Found Web Portal State & Application Logic

const STORAGE_KEY = 'campus_lost_found_web_state_v2';

// Seed initial data
const INITIAL_DATA = {
  users: [
    {
      id: 'aarav',
      name: 'Aarav Patel',
      rollNo: '2024CS042',
      role: 'student',
      dept: 'Computer Science (TY BCS)',
      email: 'aarav@campus.edu',
      phone: '9876543210',
      password: 'password123'
    },
    {
      id: 'ananya',
      name: 'Ananya Sharma',
      rollNo: '2024EC018',
      role: 'student',
      dept: 'Electronics & Comm (SY B.Tech)',
      email: 'ananya@campus.edu',
      phone: '9822334455',
      password: 'password123'
    },
    {
      id: 'priya',
      name: 'Prof. Priya Sharma',
      rollNo: 'FAC-LIB-04',
      role: 'staff',
      dept: 'Library & Information',
      email: 'priya@campus.edu',
      phone: '9812345678',
      password: 'password123'
    },
    {
      id: 'admin',
      name: 'Campus Security Desk',
      rollNo: 'SEC-ADMIN-01',
      role: 'admin',
      dept: 'Campus Security Office Room 102',
      email: 'admin@campus.edu',
      phone: '9900011223',
      password: 'admin'
    }
  ],
  categories: [
    "Electronics & Phones",
    "ID Cards & Documents",
    "Wallets & Purses",
    "Keys & Lanyards",
    "Bags & Backpacks",
    "Books & Stationery",
    "Other Belongings"
  ],
  lostItems: [
    {
      id: 'L1',
      userId: 'aarav',
      userName: 'Aarav Patel',
      userRoll: '2024CS042',
      userDept: 'Computer Science (TY BCS)',
      userEmail: 'aarav@campus.edu',
      userPhone: '9876543210',
      itemName: 'Titan Black Leather Wallet',
      category: 'Wallets & Purses',
      location: 'Campus Cafeteria',
      date: '2026-10-01',
      desc: 'Black leather wallet with college ID and driving license. Lost during afternoon lunch break.',
      reward: 'Gratitude & Treat in Cafeteria',
      status: 'Lost',
      createdAt: 1727800000000
    },
    {
      id: 'L2',
      userId: 'aarav',
      userName: 'Aarav Patel',
      userRoll: '2024CS042',
      userDept: 'Computer Science (TY BCS)',
      userEmail: 'aarav@campus.edu',
      userPhone: '9876543210',
      itemName: 'Student Smart RFID ID Card',
      category: 'ID Cards & Documents',
      location: 'Library 2nd Floor',
      date: '2026-09-30',
      desc: 'TY BCS Roll No. 2024CS042 with college blue lanyard and barcode.',
      reward: 'Needed urgently for semester exams!',
      status: 'Lost',
      createdAt: 1727700000000
    },
    {
      id: 'L3',
      userId: 'aarav',
      userName: 'Aarav Patel',
      userRoll: '2024CS042',
      userDept: 'Computer Science (TY BCS)',
      userEmail: 'aarav@campus.edu',
      userPhone: '9876543210',
      itemName: 'Casio FX-991EX Scientific Calculator',
      category: 'Electronics & Phones',
      location: 'Room 304 (Math Block)',
      date: '2026-10-01',
      desc: 'ClassWiz calculator in protective cover with initials AP written in permanent marker.',
      reward: '',
      status: 'Lost',
      createdAt: 1727810000000
    }
  ],
  foundItems: [
    {
      id: 'F1',
      userId: 'priya',
      userName: 'Prof. Priya Sharma',
      userRoll: 'FAC-LIB-04',
      userDept: 'Library & Information',
      userEmail: 'priya@campus.edu',
      userPhone: '9812345678',
      itemName: 'Titan Leather Wallet (Dark Color)',
      category: 'Wallets & Purses',
      location: 'Campus Cafeteria',
      storageLocation: 'Central Library Help Desk Room 102',
      date: '2026-10-01',
      desc: 'Found under table #6 in Cafeteria. Contains cards and currency. Safely kept at reception.',
      question: 'State the full name on the ID card inside or card count.',
      status: 'Found',
      createdAt: 1727805000000
    },
    {
      id: 'F2',
      userId: 'priya',
      userName: 'Prof. Priya Sharma',
      userRoll: 'FAC-LIB-04',
      userDept: 'Library & Information',
      userEmail: 'priya@campus.edu',
      userPhone: '9812345678',
      itemName: 'College Student ID Card (Computer Dept)',
      category: 'ID Cards & Documents',
      location: 'Library 2nd Floor',
      storageLocation: 'Library Circulation Counter',
      date: '2026-09-30',
      desc: 'Found near reference book racks on 2nd floor reading hall.',
      question: 'Verify your Student Roll number and surname.',
      status: 'Found',
      createdAt: 1727705000000
    },
    {
      id: 'F3',
      userId: 'admin',
      userName: 'Campus Security Desk',
      userRoll: 'SEC-ADMIN-01',
      userDept: 'Campus Security Office',
      userEmail: 'admin@campus.edu',
      userPhone: '9900011223',
      itemName: 'Apple AirPods Pro 2 in Case',
      category: 'Electronics & Phones',
      location: 'Main Auditorium',
      storageLocation: 'Campus Security Control Room 01',
      date: '2026-10-02',
      desc: 'White charging case found on seat G-14 after the guest lecture.',
      question: 'Pairing name or serial number / case scratch details.',
      status: 'Found',
      createdAt: 1727880000000
    }
  ],
  claims: [
    {
      id: 'C1',
      foundId: 'F1',
      itemName: 'Titan Leather Wallet (Dark Color)',
      userId: 'aarav',
      claimantName: 'Aarav Patel',
      claimantEmail: 'aarav@campus.edu',
      claimantPhone: '9876543210',
      date: '2026-10-01',
      proof: 'The wallet has my College ID card (Aarav Patel) and my blue metro card in the right pocket.',
      status: 'Pending',
      note: ''
    }
  ],
  matches: [
    {
      id: 'M1',
      lostId: 'L1',
      foundId: 'F1',
      lostName: 'Titan Black Leather Wallet',
      foundName: 'Titan Leather Wallet (Dark Color)',
      category: 'Wallets & Purses',
      score: 94,
      reason: 'Same Category and matching location: Campus Cafeteria',
      status: 'Suggested'
    },
    {
      id: 'M2',
      lostId: 'L2',
      foundId: 'F2',
      lostName: 'Student Smart RFID ID Card',
      foundName: 'College Student ID Card (Computer Dept)',
      category: 'ID Cards & Documents',
      score: 91,
      reason: 'Same Category and exact location: Library 2nd Floor',
      status: 'Suggested'
    }
  ],
  notifications: [
    {
      id: 'N1',
      userId: 'aarav',
      title: 'Potential Smart Match Detected (94%)',
      message: 'Found item "Titan Leather Wallet" at Cafeteria closely matches your report.',
      date: '2026-10-01',
      read: false
    }
  ]
};

// State Manager
class StateManager {
  constructor() {
    this.data = this.loadState();
    // Default logged in as student Aarav
    this.currentUserId = localStorage.getItem('campus_active_user_id') || 'aarav';
  }

  loadState() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) return JSON.parse(saved);
    } catch (e) {
      console.warn("Using default initial state:", e);
    }
    return JSON.parse(JSON.stringify(INITIAL_DATA));
  }

  saveState() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(this.data));
    } catch (e) {
      console.error("Storage error:", e);
    }
  }

  getCurrentUser() {
    if (!this.currentUserId) return null;
    return this.data.users.find(u => u.id === this.currentUserId) || null;
  }

  setCurrentUser(id) {
    this.currentUserId = id;
    if (id) {
      localStorage.setItem('campus_active_user_id', id);
    } else {
      localStorage.removeItem('campus_active_user_id');
    }
    this.saveState();
  }

  loginStudent(identifier, password) {
    const cleanId = identifier.trim().toLowerCase();
    const user = this.data.users.find(u => 
      u.email.toLowerCase() === cleanId || 
      (u.rollNo && u.rollNo.toLowerCase() === cleanId) ||
      u.id.toLowerCase() === cleanId
    );

    if (!user) {
      return { success: false, message: 'No student profile found with this Email or Roll Number.' };
    }

    if (user.password && user.password !== password) {
      return { success: false, message: 'Incorrect password. Try "password123".' };
    }

    this.setCurrentUser(user.id);
    return { success: true, user: user };
  }

  registerStudent(name, rollNo, dept, email, phone, password) {
    const existing = this.data.users.find(u => 
      u.email.toLowerCase() === email.trim().toLowerCase() ||
      (u.rollNo && u.rollNo.toLowerCase() === rollNo.trim().toLowerCase())
    );

    if (existing) {
      return { success: false, message: 'An account with this Email or Roll Number already exists.' };
    }

    const newId = 'student_' + Date.now();
    const newUser = {
      id: newId,
      name: name.trim(),
      rollNo: rollNo.trim().toUpperCase(),
      role: 'student',
      dept: dept.trim(),
      email: email.trim().toLowerCase(),
      phone: phone.trim(),
      password: password
    };

    this.data.users.push(newUser);
    this.setCurrentUser(newId);
    return { success: true, user: newUser };
  }

  logout() {
    this.setCurrentUser(null);
  }
}

const appState = new StateManager();

// App Initialization
document.addEventListener('DOMContentLoaded', () => {
  setupNavigation();
  setupUserSwitcher();
  setupCatalogFilters();
  setupAuthHandlers();
  setupForms();
  renderAll();
});

function setupNavigation() {
  document.querySelectorAll('.nav-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      const targetTab = btn.getAttribute('data-tab');
      switchTab(targetTab);
    });
  });

  // Sub-tabs (in Matches & My Items)
  document.querySelectorAll('.sub-tab').forEach(btn => {
    btn.addEventListener('click', () => {
      const parent = btn.closest('section');
      parent.querySelectorAll('.sub-tab').forEach(b => b.classList.remove('active'));
      parent.querySelectorAll('.sub-content').forEach(c => c.classList.remove('active'));

      btn.classList.add('active');
      const targetSub = btn.getAttribute('data-subtab');
      const subElem = document.getElementById(targetSub);
      if (subElem) subElem.classList.add('active');
    });
  });

  // Notifications Drawer
  document.getElementById('open-notif-btn').addEventListener('click', () => {
    document.getElementById('notif-drawer').classList.add('active');
    renderNotifications();
  });
}

function switchTab(tabId, subfilter = null) {
  document.querySelectorAll('.nav-btn').forEach(b => {
    b.classList.toggle('active', b.getAttribute('data-tab') === tabId);
  });

  document.querySelectorAll('.tab-view').forEach(v => {
    v.classList.toggle('active', v.id === `view-${tabId}`);
  });

  if (tabId === 'browse' && subfilter) {
    document.querySelectorAll('.seg-btn').forEach(btn => {
      btn.classList.toggle('active', btn.getAttribute('data-type') === subfilter);
    });
    renderCatalog();
  }

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function setupUserSwitcher() {
  const selector = document.getElementById('user-selector');
  if (appState.currentUserId) {
    selector.value = appState.currentUserId;
  }
  selector.addEventListener('change', (e) => {
    appState.setCurrentUser(e.target.value);
    showToast(`Switched account to ${appState.getCurrentUser().name}`);
    renderAll();
  });
}

function setupAuthHandlers() {
  // Open Auth Modal
  document.getElementById('open-auth-btn').addEventListener('click', () => openAuthModal('login'));
  
  // Logout
  document.getElementById('logout-btn').addEventListener('click', () => {
    const user = appState.getCurrentUser();
    appState.logout();
    showToast(`Signed out of ${user ? user.name : 'account'}. Please log in to post items.`);
    renderAll();
  });

  // Student Login Form
  document.getElementById('student-login-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const idInput = document.getElementById('login-email').value;
    const passInput = document.getElementById('login-password').value;

    const res = appState.loginStudent(idInput, passInput);
    if (res.success) {
      closeModal('modal-auth');
      showToast(`Welcome back, ${res.user.name}! You can now report lost or found items.`);
      renderAll();
    } else {
      alert(res.message);
    }
  });

  // Student Register Form
  document.getElementById('student-register-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const name = document.getElementById('reg-name').value;
    const roll = document.getElementById('reg-roll').value;
    const dept = document.getElementById('reg-dept').value;
    const email = document.getElementById('reg-email').value;
    const phone = document.getElementById('reg-phone').value;
    const pass = document.getElementById('reg-password').value;

    const res = appState.registerStudent(name, roll, dept, email, phone, pass);
    if (res.success) {
      closeModal('modal-auth');
      showToast(`Student account registered! Logged in as ${res.user.name} (${res.user.rollNo}).`);
      renderAll();
    } else {
      alert(res.message);
    }
  });
}

function openAuthModal(tab = 'login') {
  switchAuthTab(tab);
  document.getElementById('modal-auth').classList.add('active');
}

function switchAuthTab(tab) {
  const isLogin = tab === 'login';
  document.getElementById('tab-btn-login').classList.toggle('active', isLogin);
  document.getElementById('tab-btn-register').classList.toggle('active', !isLogin);
  document.getElementById('student-login-form').style.display = isLogin ? 'block' : 'none';
  document.getElementById('student-register-form').style.display = isLogin ? 'none' : 'block';
  document.getElementById('auth-title').textContent = isLogin ? 'Student Portal Login' : 'New Student Registration';
  document.getElementById('auth-subtitle').textContent = isLogin ? 
    'Login with your college email or roll number to add lost/found items' : 
    'Register your student details to easily report and recover lost belongings';
}

function quickLoginAarav() {
  appState.setCurrentUser('aarav');
  closeModal('modal-auth');
  showToast('Logged in as Aarav Patel (Student - TY BCS)!');
  renderAll();
}

function setupCatalogFilters() {
  const searchInput = document.getElementById('catalog-search');
  searchInput.addEventListener('input', () => renderCatalog());

  document.getElementById('clear-search-btn').addEventListener('click', () => {
    searchInput.value = '';
    renderCatalog();
  });

  document.querySelectorAll('.seg-btn').forEach(btn => {
    btn.addEventListener('click', () => {
      document.querySelectorAll('.seg-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      renderCatalog();
    });
  });

  document.getElementById('location-filter').addEventListener('change', () => renderCatalog());
}

function setupForms() {
  // Report Form toggle
  const reportTypeRadios = document.querySelectorAll('input[name="reportType"]');
  reportTypeRadios.forEach(radio => {
    radio.addEventListener('change', (e) => {
      const isFound = e.target.value === 'found';
      document.getElementById('found-only-fields').style.display = isFound ? 'block' : 'none';
      document.getElementById('reward-group').style.display = isFound ? 'none' : 'block';
      document.getElementById('report-submit-btn').textContent = isFound ? 'Submit Found Item Deposit' : 'Submit Lost Item Report';
    });
  });

  document.getElementById('open-report-btn').addEventListener('click', () => openReportModal());

  // Report Form Submission
  document.getElementById('report-form').addEventListener('submit', (e) => {
    e.preventDefault();
    handleReportSubmit();
  });

  // Claim Form Submission
  document.getElementById('claim-form').addEventListener('submit', (e) => {
    e.preventDefault();
    handleClaimSubmit();
  });

  // Add Category Form
  document.getElementById('add-cat-form').addEventListener('submit', (e) => {
    e.preventDefault();
    const name = document.getElementById('new-cat-name').value.trim();
    if (name) {
      if (!appState.data.categories.includes(name)) {
        appState.data.categories.push(name);
        appState.saveState();
        closeModal('modal-add-cat');
        showToast(`Category "${name}" added!`);
        renderCategories();
      }
    }
  });
}

function renderAll() {
  const user = appState.getCurrentUser();
  const profileBadge = document.getElementById('user-profile-badge');
  const loginBtn = document.getElementById('open-auth-btn');
  const heroStudentBtn = document.getElementById('hero-student-login-btn');
  const userSelector = document.getElementById('user-selector');

  if (user) {
    profileBadge.style.display = 'flex';
    loginBtn.style.display = 'none';
    if (heroStudentBtn) heroStudentBtn.style.display = 'none';

    document.getElementById('user-display-name').textContent = user.name;
    document.getElementById('user-display-role').textContent = `${user.role.toUpperCase()} • ${user.rollNo || user.dept}`;
    
    // Initials
    const initials = user.name.split(' ').map(n => n[0]).join('').substring(0, 2).toUpperCase();
    document.getElementById('user-avatar-initials').textContent = initials;

    document.getElementById('welcome-text').textContent = `Welcome back, ${user.name.split(' ')[0]}! 👋`;
    document.getElementById('welcome-sub').textContent = user.role === 'student' ?
      `Logged in as Student (${user.rollNo} • ${user.dept}). Easily report lost campus belongings or deposit found property.` :
      `Staff & Campus Administration portal view.`;

    userSelector.value = user.id;
  } else {
    profileBadge.style.display = 'none';
    loginBtn.style.display = 'inline-flex';
    if (heroStudentBtn) heroStudentBtn.style.display = 'inline-flex';

    document.getElementById('welcome-text').textContent = `Campus Student Lost & Found Portal`;
    document.getElementById('welcome-sub').textContent = `Please sign in with your student credentials to post lost belongings, report found items, and match claims with campus security.`;
  }

  // Show/Hide Admin Nav based on role
  const adminBtn = document.getElementById('admin-nav-btn');
  adminBtn.style.display = (user && user.role === 'admin') ? 'block' : 'none';

  renderStats();
  renderCategories();
  renderDashboardLists();
  renderCatalog();
  renderMatchesAndClaims();
  renderMyItems();
  renderAdmin();
  renderNotifications();
}

function renderStats() {
  const lostActive = appState.data.lostItems.filter(i => i.status === 'Lost').length;
  const foundActive = appState.data.foundItems.filter(i => i.status === 'Found').length;
  const matchesCount = appState.data.matches.filter(m => m.status === 'Suggested').length;
  const recoveredCount = appState.data.lostItems.filter(i => i.status === 'Resolved').length +
                         appState.data.foundItems.filter(i => i.status === 'Resolved').length;

  document.getElementById('stat-lost-count').textContent = lostActive;
  document.getElementById('stat-found-count').textContent = foundActive;
  document.getElementById('stat-match-count').textContent = matchesCount;
  document.getElementById('stat-recovered-count').textContent = recoveredCount;
  document.getElementById('match-badge').textContent = matchesCount;
}

function renderCategories() {
  const container = document.getElementById('categories-container');
  const formCatSelect = document.getElementById('form-category');
  
  container.innerHTML = `<button class="cat-pill active" onclick="selectCatFilter('')">All Categories</button>` +
    appState.data.categories.map(c => `
      <button class="cat-pill" onclick="selectCatFilter('${c}')">${c}</button>
    `).join('');

  formCatSelect.innerHTML = appState.data.categories.map(c => `
    <option value="${c}">${c}</option>
  `).join('');

  // Admin categories list
  const adminList = document.getElementById('admin-categories-list');
  if (adminList) {
    adminList.innerHTML = appState.data.categories.map(c => `
      <span class="cat-pill">${c}</span>
    `).join(' ');
  }
}

let activeCategoryFilter = '';
function selectCatFilter(cat) {
  activeCategoryFilter = cat;
  document.querySelectorAll('.cat-pill').forEach(p => {
    p.classList.toggle('active', (cat === '' && p.textContent === 'All Categories') || p.textContent === cat);
  });
  renderCatalog();
}

function renderDashboardLists() {
  const lostList = document.getElementById('dash-lost-list');
  const foundList = document.getElementById('dash-found-list');

  const recentLost = appState.data.lostItems.filter(i => i.status === 'Lost').slice(0, 3);
  const recentFound = appState.data.foundItems.filter(i => i.status === 'Found').slice(0, 3);

  lostList.innerHTML = recentLost.length ? recentLost.map(i => renderItemCardHTML(i, 'lost')).join('') :
    '<p class="item-desc">No active lost reports. All caught up!</p>';

  foundList.innerHTML = recentFound.length ? recentFound.map(i => renderItemCardHTML(i, 'found')).join('') :
    '<p class="item-desc">No newly deposited found items right now.</p>';
}

let showRecoveredCatalog = false;
function toggleShowRecovered() {
  showRecoveredCatalog = !showRecoveredCatalog;
  const btn = document.getElementById('toggle-recovered-btn');
  if (btn) {
    btn.textContent = showRecoveredCatalog ? 'Showing All (Inc. Recovered)' : 'Active Listings Only';
    btn.classList.toggle('active', showRecoveredCatalog);
  }
  renderCatalog();
}

function renderCatalog() {
  const query = document.getElementById('catalog-search').value.toLowerCase().trim();
  const activeTypeBtn = document.querySelector('.seg-btn.active');
  const typeFilter = activeTypeBtn ? activeTypeBtn.getAttribute('data-type') : 'all';
  const locationFilter = document.getElementById('location-filter').value;

  let items = [];

  const activeLost = appState.data.lostItems.filter(i => i.status === 'Lost');
  const activeFound = appState.data.foundItems.filter(i => i.status === 'Found');

  if (typeFilter === 'all' || typeFilter === 'lost') {
    items.push(...appState.data.lostItems.filter(i => showRecoveredCatalog || i.status === 'Lost').map(i => ({ ...i, itemType: 'lost' })));
  }
  if (typeFilter === 'all' || typeFilter === 'found') {
    items.push(...appState.data.foundItems.filter(i => showRecoveredCatalog || i.status === 'Found').map(i => ({ ...i, itemType: 'found' })));
  }

  // Apply filters
  const filtered = items.filter(i => {
    const matchQuery = !query || i.itemName.toLowerCase().includes(query) ||
                       i.desc.toLowerCase().includes(query) ||
                       i.location.toLowerCase().includes(query) ||
                       (i.userRoll && i.userRoll.toLowerCase().includes(query));
    const matchCat = !activeCategoryFilter || i.category === activeCategoryFilter;
    const matchLoc = !locationFilter || i.location.toLowerCase().includes(locationFilter.toLowerCase());
    return matchQuery && matchCat && matchLoc;
  });

  // Update counts
  document.getElementById('count-all').textContent = activeLost.length + activeFound.length;
  document.getElementById('count-lost').textContent = activeLost.length;
  document.getElementById('count-found').textContent = activeFound.length;

  const grid = document.getElementById('catalog-grid');
  if (filtered.length === 0) {
    grid.innerHTML = `
      <div style="grid-column: 1 / -1; text-align: center; padding: 48px; background: white; border-radius: 12px;">
        <h3>No matching items found</h3>
        <p style="color: var(--text-muted); margin-top: 6px;">Try adjusting your search terms or filters.</p>
      </div>
    `;
  } else {
    grid.innerHTML = filtered.map(i => renderItemCardHTML(i, i.itemType)).join('');
  }
}

function renderItemCardHTML(item, type) {
  const isLost = type === 'lost';
  const badgeClass = item.status === 'Resolved' ? 'badge-resolved' :
                     item.status === 'Claimed' ? 'badge-claimed' :
                     isLost ? 'badge-lost' : 'badge-found';

  const currentUser = appState.getCurrentUser();
  const isOwner = currentUser && item.userId === currentUser.id;
  const isAdmin = currentUser && currentUser.role === 'admin';

  return `
    <div class="item-card">
      <div class="item-top">
        <div>
          <span class="item-cat">${item.category}</span>
          <h3 class="item-title">${escapeHTML(item.itemName)}</h3>
        </div>
        <span class="status-badge ${badgeClass}">${item.status === 'Resolved' ? 'RECOVERED' : item.status}</span>
      </div>
      <p class="item-desc">${escapeHTML(item.desc)}</p>
      ${item.storageLocation ? `
        <div class="item-storage">🛡️ Safely kept at: ${escapeHTML(item.storageLocation)}</div>
      ` : ''}
      <div class="item-meta">
        <span>📍 ${escapeHTML(item.location)}</span>
        <span>📅 ${item.date}</span>
      </div>

      ${isLost && (isOwner || isAdmin) && item.status === 'Lost' ? `
        <button class="btn btn-found" style="width: 100%; margin-top: 8px; font-weight: 700; font-size: 0.8rem; background: #059669; color: white;" onclick="confirmReceivedObject('${item.id}')">
          🎉 I Got My Object Back! (Remove from List)
        </button>
      ` : ''}

      <div class="card-actions">
        <button class="btn btn-secondary" style="flex: 1;" onclick="openDetailModal('${item.id}', '${type}')">View Details</button>
        ${!isLost && item.status === 'Found' ? `
          <button class="btn btn-primary" style="flex: 1;" onclick="openClaimModal('${item.id}')">Claim Item</button>
        ` : ''}
      </div>
    </div>
  `;
}

function openDetailModal(id, type) {
  const isLost = type === 'lost';
  const item = isLost ? appState.data.lostItems.find(i => i.id === id) :
                        appState.data.foundItems.find(i => i.id === id);
  if (!item) return;

  const currentUser = appState.getCurrentUser();
  const isOwner = currentUser && item.userId === currentUser.id;
  const isAdmin = currentUser && currentUser.role === 'admin';

  document.getElementById('detail-badge-wrap').innerHTML = `
    <span class="status-badge ${item.status === 'Resolved' ? 'badge-resolved' : isLost ? 'badge-lost' : 'badge-found'}">
      ${isLost ? 'LOST REPORT' : 'FOUND ITEM'} • ${item.status === 'Resolved' ? 'RECOVERED' : item.status}
    </span>
  `;

  document.getElementById('detail-body').innerHTML = `
    <h2 style="font-size: 1.4rem; font-weight: 800; margin-bottom: 4px;">${escapeHTML(item.itemName)}</h2>
    <p style="color: var(--primary); font-weight: 600; font-size: 0.9rem; margin-bottom: 16px;">${item.category}</p>
    
    ${isLost && (isOwner || isAdmin) ? `
      <div style="background: ${item.status === 'Lost' ? '#ECFDF5' : '#F1F5F9'}; border: 1px solid ${item.status === 'Lost' ? '#A7F3D0' : '#CBD5E1'}; border-radius: 12px; padding: 16px; margin-bottom: 16px;">
        <div style="display: flex; align-items: center; gap: 8px; font-weight: 700; color: ${item.status === 'Lost' ? '#065F46' : '#334155'}; font-size: 1rem;">
          <span>${item.status === 'Lost' ? '🎉 Did you get your object back?' : '✅ Object Recovered & Removed'}</span>
        </div>
        <p style="font-size: 0.85rem; color: #475569; margin: 6px 0 12px 0;">
          ${item.status === 'Lost' ? 
            `Once you have received your lost "${escapeHTML(item.itemName)}" back from campus custody or the finder, tap below to remove it from the active campus lost devices list.` : 
            `This object has been marked as recovered and removed from the active campus lost devices list.`}
        </p>
        ${item.status === 'Lost' ? `
          <button class="btn btn-found" style="font-weight: 700; width: 100%; background: #059669; color: white;" onclick="confirmReceivedObject('${item.id}')">
            ✅ I Received It! Remove from Lost List
          </button>
        ` : `
          <button class="btn btn-secondary" onclick="toggleItemStatus('${item.id}', 'lost')">Re-open Lost Listing</button>
        `}
      </div>
    ` : ''}

    <div style="background: var(--surface-alt); padding: 16px; border-radius: 12px; margin-bottom: 16px;">
      <h4 style="font-size: 0.85rem; font-weight: 700; color: var(--text-muted); text-transform: uppercase;">Description</h4>
      <p style="margin-top: 4px; font-size: 0.95rem;">${escapeHTML(item.desc)}</p>
    </div>

    ${item.storageLocation ? `
      <div style="background: var(--primary-bg); padding: 14px; border-radius: 12px; margin-bottom: 16px; color: var(--primary); font-size: 0.9rem;">
        <strong>Custody Location:</strong> ${escapeHTML(item.storageLocation)}
      </div>
    ` : ''}

    ${item.reward ? `
      <div style="background: var(--accent-bg); padding: 14px; border-radius: 12px; margin-bottom: 16px; color: var(--accent); font-size: 0.9rem;">
        <strong>⭐ Student Note / Reward:</strong> ${escapeHTML(item.reward)}
      </div>
    ` : ''}

    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 20px;">
      <div>
        <span style="font-size: 0.8rem; color: var(--text-muted);">Campus Location</span>
        <div style="font-weight: 600;">📍 ${escapeHTML(item.location)}</div>
      </div>
      <div>
        <span style="font-size: 0.8rem; color: var(--text-muted);">Reported Date</span>
        <div style="font-weight: 600;">📅 ${item.date}</div>
      </div>
    </div>

    <div style="border-top: 1px solid var(--border); padding-top: 16px;">
      <h4 style="font-size: 0.85rem; color: var(--text-muted); text-transform: uppercase; margin-bottom: 8px;">
        ${isLost ? 'Student Reporter Info' : 'Finder Information'}
      </h4>
      <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 12px;">
        <div>
          <div style="font-weight: 700;">${escapeHTML(item.userName)} ${item.userRoll ? `(${escapeHTML(item.userRoll)})` : ''}</div>
          <div style="font-size: 0.85rem; color: var(--text-muted);">${escapeHTML(item.userDept || 'Student')} • ${escapeHTML(item.userEmail)}</div>
        </div>
        <div style="display: flex; gap: 8px;">
          <a href="tel:${item.userPhone}" class="btn btn-secondary">📞 Call</a>
          <a href="mailto:${item.userEmail}?subject=Campus%20Lost%20Found:%20${encodeURIComponent(item.itemName)}" class="btn btn-secondary">✉️ Email</a>
        </div>
      </div>
    </div>
  `;

  let footerHTML = '';
  if (!isLost && item.status === 'Found' && !isOwner) {
    footerHTML += `<button class="btn btn-primary" onclick="closeModal('modal-detail'); openClaimModal('${item.id}')">Claim This Item</button>`;
  }

  if (isOwner || isAdmin) {
    if (isLost && item.status === 'Lost') {
      footerHTML += `
        <button class="btn btn-found" style="font-weight: 700; background: #059669; color: white;" onclick="confirmReceivedObject('${item.id}')">
          ✅ Remove from Lost List (Mark Recovered)
        </button>
        <button class="btn btn-lost" onclick="deleteItem('${item.id}', 'lost')">Delete Report</button>
      `;
    } else {
      footerHTML += `
        <button class="btn btn-secondary" onclick="toggleItemStatus('${item.id}', '${type}')">
          ${item.status === 'Resolved' ? 'Re-open Listing' : 'Mark as Resolved / Returned'}
        </button>
        <button class="btn btn-lost" onclick="deleteItem('${item.id}', '${type}')">Delete Listing</button>
      `;
    }
  }

  document.getElementById('detail-footer').innerHTML = footerHTML;
  document.getElementById('modal-detail').classList.add('active');
}

function openClaimModal(foundId) {
  const user = appState.getCurrentUser();
  if (!user) {
    showToast('Please log in with your student account first to submit an ownership claim.');
    openAuthModal('login');
    return;
  }

  const item = appState.data.foundItems.find(i => i.id === foundId);
  if (!item) return;

  document.getElementById('claim-target-id').value = foundId;
  const qBox = document.getElementById('claim-question-box');
  if (item.question) {
    qBox.style.display = 'block';
    qBox.innerHTML = `<strong>Finder's Verification Question:</strong><p>${escapeHTML(item.question)}</p>`;
  } else {
    qBox.style.display = 'none';
  }

  document.getElementById('claim-proof').value = '';
  document.getElementById('modal-claim').classList.add('active');
}

function handleClaimSubmit() {
  const user = appState.getCurrentUser();
  if (!user) {
    openAuthModal('login');
    return;
  }

  const foundId = document.getElementById('claim-target-id').value;
  const proof = document.getElementById('claim-proof').value.trim();
  const foundItem = appState.data.foundItems.find(i => i.id === foundId);

  if (!proof || !foundItem) return;

  const newClaim = {
    id: 'C_' + Date.now(),
    foundId: foundId,
    itemName: foundItem.itemName,
    userId: user.id,
    claimantName: user.name,
    claimantRoll: user.rollNo || '',
    claimantEmail: user.email,
    claimantPhone: user.phone,
    date: new Date().toISOString().split('T')[0],
    proof: proof,
    status: 'Pending',
    note: ''
  };

  appState.data.claims.push(newClaim);

  // Notify finder
  appState.data.notifications.push({
    id: 'N_' + Date.now(),
    userId: foundItem.userId,
    title: `New Claim for "${foundItem.itemName}"`,
    message: `${user.name} (${user.rollNo || 'Student'}) submitted ownership verification proof.`,
    date: new Date().toISOString().split('T')[0],
    read: false
  });

  appState.saveState();
  closeModal('modal-claim');
  showToast('Claim submitted! The finder and security will review your proof.');
  renderAll();
}

function openReportModal(defaultType = 'lost') {
  const user = appState.getCurrentUser();
  if (!user) {
    showToast('Please sign in with your student credentials to report lost or found items.');
    openAuthModal('login');
    return;
  }

  document.querySelector(`input[name="reportType"][value="${defaultType}"]`).checked = true;
  document.getElementById('found-only-fields').style.display = defaultType === 'found' ? 'block' : 'none';
  document.getElementById('reward-group').style.display = defaultType === 'found' ? 'none' : 'block';
  document.getElementById('report-submit-btn').textContent = defaultType === 'found' ? 'Submit Found Item Deposit' : 'Submit Lost Item Report';
  document.getElementById('form-date').value = new Date().toISOString().split('T')[0];
  
  // Fill student reporter banner & form
  document.getElementById('rep-student-name').textContent = user.name;
  document.getElementById('rep-student-dept').textContent = `${user.dept} • Roll ${user.rollNo || 'N/A'}`;
  document.getElementById('rep-student-email').textContent = user.email;
  document.getElementById('rep-student-phone').textContent = user.phone;
  document.getElementById('form-contact').value = user.phone;

  document.getElementById('modal-report').classList.add('active');
}

function handleReportSubmit() {
  const user = appState.getCurrentUser();
  if (!user) {
    openAuthModal('login');
    return;
  }

  const isLost = document.querySelector('input[name="reportType"]:checked').value === 'lost';
  const name = document.getElementById('form-item-name').value.trim();
  const cat = document.getElementById('form-category').value;
  const date = document.getElementById('form-date').value;
  const loc = document.getElementById('form-location').value.trim();
  const contact = document.getElementById('form-contact').value.trim();
  const desc = document.getElementById('form-desc').value.trim();

  if (isLost) {
    const reward = document.getElementById('form-reward').value.trim();
    const newLost = {
      id: 'L_' + Date.now(),
      userId: user.id,
      userName: user.name,
      userRoll: user.rollNo || '',
      userDept: user.dept || '',
      userEmail: user.email,
      userPhone: contact || user.phone,
      itemName: name,
      category: cat,
      location: loc,
      date: date,
      desc: desc,
      reward: reward,
      status: 'Lost',
      createdAt: Date.now()
    };
    appState.data.lostItems.unshift(newLost);
    runSmartMatcherForLost(newLost);
  } else {
    const storage = document.getElementById('form-storage').value.trim();
    const question = document.getElementById('form-question').value.trim();
    const newFound = {
      id: 'F_' + Date.now(),
      userId: user.id,
      userName: user.name,
      userRoll: user.rollNo || '',
      userDept: user.dept || '',
      userEmail: user.email,
      userPhone: contact || user.phone,
      itemName: name,
      category: cat,
      location: loc,
      storageLocation: storage || 'Campus Security Desk (Room 102)',
      date: date,
      desc: desc,
      question: question,
      status: 'Found',
      createdAt: Date.now()
    };
    appState.data.foundItems.unshift(newFound);
    runSmartMatcherForFound(newFound);
  }

  appState.saveState();
  closeModal('modal-report');
  document.getElementById('report-form').reset();
  showToast(isLost ? 'Lost report posted! Automatic matcher checked for matching items.' : 'Found item deposited! Matcher notified potential owners.');
  renderAll();
  switchTab('browse');
}

function runSmartMatcherForLost(lost) {
  appState.data.foundItems.forEach(found => {
    if (found.category === lost.category) {
      const matchScore = 88;
      appState.data.matches.unshift({
        id: 'M_' + Date.now(),
        lostId: lost.id,
        foundId: found.id,
        lostName: lost.itemName,
        foundName: found.itemName,
        category: lost.category,
        score: matchScore,
        reason: `Same category (${lost.category}) and campus area`,
        status: 'Suggested'
      });
      appState.data.notifications.unshift({
        id: 'N_' + Date.now(),
        userId: lost.userId,
        title: `Potential Match Detected (${matchScore}%)`,
        message: `Found item "${found.itemName}" may match your report!`,
        date: new Date().toISOString().split('T')[0],
        read: false
      });
    }
  });
}

function runSmartMatcherForFound(found) {
  appState.data.lostItems.forEach(lost => {
    if (lost.category === found.category) {
      const matchScore = 88;
      appState.data.matches.unshift({
        id: 'M_' + Date.now(),
        lostId: lost.id,
        foundId: found.id,
        lostName: lost.itemName,
        foundName: found.itemName,
        category: found.category,
        score: matchScore,
        reason: `Matching category (${found.category})`,
        status: 'Suggested'
      });
      appState.data.notifications.unshift({
        id: 'N_' + Date.now(),
        userId: lost.userId,
        title: `Smart Match Detected (${matchScore}%)`,
        message: `Found item "${found.itemName}" matches your lost item report!`,
        date: new Date().toISOString().split('T')[0],
        read: false
      });
    }
  });
}

function renderMatchesAndClaims() {
  const matchesList = document.getElementById('matches-list');
  const incomingList = document.getElementById('incoming-claims-list');
  const myClaimsList = document.getElementById('my-claims-list');
  const user = appState.getCurrentUser();

  // Matches
  const matches = appState.data.matches;
  document.getElementById('sub-matches-count').textContent = matches.length;
  matchesList.innerHTML = matches.length ? matches.map(m => `
    <div class="match-card">
      <div class="match-header">
        <span class="match-score-badge">✨ ${m.score}% Match Confidence</span>
        <span class="status-badge ${m.status === 'Accepted' ? 'badge-found' : 'badge-resolved'}">${m.status}</span>
      </div>
      <div class="match-comparison">
        <div class="match-item-row"><strong>🔴 Lost:</strong> ${escapeHTML(m.lostName)}</div>
        <div class="match-item-row"><strong>🟢 Found:</strong> ${escapeHTML(m.foundName)}</div>
        <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 6px;">Reason: ${escapeHTML(m.reason)}</div>
      </div>
      ${m.status === 'Suggested' ? `
        <div class="card-actions">
          <button class="btn btn-secondary" style="flex: 1;" onclick="setMatchStatus('${m.id}', 'Dismissed')">Not a Match</button>
          <button class="btn btn-primary" style="flex: 1;" onclick="setMatchStatus('${m.id}', 'Accepted')">Confirm Match</button>
        </div>
      ` : ''}
    </div>
  `).join('') : '<p>No automatic matches at this time.</p>';

  // Incoming Claims
  const myFoundIds = user ? new Set(appState.data.foundItems.filter(f => f.userId === user.id).map(f => f.id)) : new Set();
  const incoming = appState.data.claims.filter(c => (user && user.role === 'admin') || myFoundIds.has(c.foundId));
  document.getElementById('sub-claims-count').textContent = incoming.length;

  incomingList.innerHTML = incoming.length ? incoming.map(c => `
    <div class="item-card" style="margin-bottom: 12px;">
      <div class="item-top">
        <div>
          <h3 class="item-title">Claim for: ${escapeHTML(c.itemName)}</h3>
          <span style="font-size: 0.8rem; color: var(--text-muted);">Claimant: ${escapeHTML(c.claimantName)} ${c.claimantRoll ? `(${c.claimantRoll})` : ''} • ${c.claimantPhone}</span>
        </div>
        <span class="status-badge ${c.status === 'Approved' ? 'badge-found' : c.status === 'Rejected' ? 'badge-lost' : 'badge-claimed'}">${c.status}</span>
      </div>
      <div style="background: var(--surface-alt); padding: 12px; border-radius: 8px; margin: 8px 0; font-size: 0.9rem;">
        <strong>Submitted Proof:</strong> "${escapeHTML(c.proof)}"
      </div>
      ${c.status === 'Pending' ? `
        <div class="card-actions">
          <button class="btn btn-lost" onclick="resolveClaim('${c.id}', false)">Reject Claim</button>
          <button class="btn btn-found" onclick="resolveClaim('${c.id}', true)">Approve Claim</button>
        </div>
      ` : ''}
    </div>
  `).join('') : '<p>No incoming claims to review.</p>';

  // My Claims
  const myClaims = user ? appState.data.claims.filter(c => c.userId === user.id) : [];
  document.getElementById('sub-myclaims-count').textContent = myClaims.length;

  myClaimsList.innerHTML = myClaims.length ? myClaims.map(c => `
    <div class="item-card" style="margin-bottom: 12px;">
      <div class="item-top">
        <h3 class="item-title">${escapeHTML(c.itemName)}</h3>
        <span class="status-badge ${c.status === 'Approved' ? 'badge-found' : 'badge-claimed'}">${c.status}</span>
      </div>
      <p style="font-size: 0.88rem; color: var(--text-muted); margin: 6px 0;">Proof: ${escapeHTML(c.proof)}</p>
      <div style="font-size: 0.8rem; color: var(--text-muted);">Filed Date: ${c.date}</div>
    </div>
  `).join('') : '<p>You have not filed any item claims.</p>';
}

function setMatchStatus(matchId, status) {
  const match = appState.data.matches.find(m => m.id === matchId);
  if (match) {
    match.status = status;
    appState.saveState();
    renderMatchesAndClaims();
    renderStats();
    showToast(`Match status updated to ${status}`);
  }
}

function resolveClaim(claimId, approved) {
  const claim = appState.data.claims.find(c => c.id === claimId);
  if (!claim) return;

  claim.status = approved ? 'Approved' : 'Rejected';
  if (approved) {
    const foundItem = appState.data.foundItems.find(f => f.id === claim.foundId);
    if (foundItem) foundItem.status = 'Claimed';

    appState.data.notifications.push({
      id: 'N_' + Date.now(),
      userId: claim.userId,
      title: 'Claim Approved! 🎉',
      message: `Your claim for "${claim.itemName}" was approved! Contact security/finder to retrieve it.`,
      date: new Date().toISOString().split('T')[0],
      read: false
    });
  }

  appState.saveState();
  renderAll();
  showToast(approved ? 'Claim approved and owner notified!' : 'Claim rejected.');
}

function renderMyItems() {
  const user = appState.getCurrentUser();
  const myLost = user ? appState.data.lostItems.filter(i => i.userId === user.id) : [];
  const myFound = user ? appState.data.foundItems.filter(i => i.userId === user.id) : [];

  document.getElementById('my-lost-count').textContent = myLost.length;
  document.getElementById('my-found-count').textContent = myFound.length;

  const myLostGrid = document.getElementById('my-lost-grid');
  const myFoundGrid = document.getElementById('my-found-grid');

  myLostGrid.innerHTML = myLost.length ? myLost.map(i => renderItemCardHTML(i, 'lost')).join('') :
    '<p>You have not posted any lost items. Click "+ Report Item" to add one.</p>';

  myFoundGrid.innerHTML = myFound.length ? myFound.map(i => renderItemCardHTML(i, 'found')).join('') :
    '<p>You have not posted any found items. Click "+ Report Item" to deposit one.</p>';
}

function renderAdmin() {
  const totalItems = appState.data.lostItems.length + appState.data.foundItems.length;
  const resolved = appState.data.lostItems.filter(i => i.status === 'Resolved').length +
                   appState.data.foundItems.filter(i => i.status === 'Resolved').length;
  const rate = totalItems > 0 ? Math.round((resolved / totalItems) * 100) : 0;

  document.getElementById('admin-rate').textContent = `${rate}%`;
  document.getElementById('admin-users-count').textContent = appState.data.users.length;
  document.getElementById('admin-total-items').textContent = totalItems;
  document.getElementById('admin-pending-claims').textContent = appState.data.claims.filter(c => c.status === 'Pending').length;

  const tbody = document.querySelector('#users-table tbody');
  if (tbody) {
    tbody.innerHTML = appState.data.users.map(u => `
      <tr>
        <td><strong>${escapeHTML(u.name)}</strong></td>
        <td><span class="role-badge role-${u.role}">${u.role.toUpperCase()}</span></td>
        <td>${escapeHTML(u.dept)} ${u.rollNo ? `(Roll: ${escapeHTML(u.rollNo)})` : ''}</td>
        <td>${escapeHTML(u.email)} • ${escapeHTML(u.phone)}</td>
      </tr>
    `).join('');
  }
}

function renderNotifications() {
  const user = appState.getCurrentUser();
  const notifs = user ? appState.data.notifications.filter(n => n.userId === user.id) : [];
  const unreadCount = notifs.filter(n => !n.read).length;

  const countBadge = document.getElementById('notif-count');
  countBadge.textContent = unreadCount;
  countBadge.style.display = unreadCount > 0 ? 'inline-block' : 'none';

  const list = document.getElementById('notif-list');
  list.innerHTML = notifs.length ? notifs.map(n => `
    <div class="notif-item ${!n.read ? 'unread' : ''}">
      <strong style="font-size: 0.88rem;">${escapeHTML(n.title)}</strong>
      <p style="font-size: 0.82rem; margin: 4px 0;">${escapeHTML(n.message)}</p>
      <span style="font-size: 0.72rem; color: var(--text-muted);">${n.date}</span>
    </div>
  `).join('') : '<p style="text-align: center; color: var(--text-muted); padding: 20px;">No notifications.</p>';
}

function markAllNotificationsRead() {
  const user = appState.getCurrentUser();
  if (user) {
    appState.data.notifications.forEach(n => {
      if (n.userId === user.id) n.read = true;
    });
    appState.saveState();
    renderNotifications();
    showToast('All notifications marked as read.');
  }
}

function toggleItemStatus(id, type) {
  const isLost = type === 'lost';
  const item = isLost ? appState.data.lostItems.find(i => i.id === id) :
                        appState.data.foundItems.find(i => i.id === id);
  if (!item) return;

  item.status = item.status === 'Resolved' ? (isLost ? 'Lost' : 'Found') : 'Resolved';
  appState.saveState();
  closeModal('modal-detail');
  showToast(`Item status updated to ${item.status}!`);
  renderAll();
}

function deleteItem(id, type) {
  if (!confirm("Are you sure you want to remove this item report?")) return;
  const isLost = type === 'lost';
  if (isLost) {
    appState.data.lostItems = appState.data.lostItems.filter(i => i.id !== id);
  } else {
    appState.data.foundItems = appState.data.foundItems.filter(i => i.id !== id);
  }
  appState.saveState();
  closeModal('modal-detail');
  showToast('Item deleted.');
  renderAll();
}

function setFormLocation(loc) {
  document.getElementById('form-location').value = loc;
}

function openAddCategoryModal() {
  document.getElementById('modal-add-cat').classList.add('active');
}

function closeModal(modalId) {
  document.getElementById(modalId).classList.remove('active');
}

function closeDrawer() {
  document.getElementById('notif-drawer').classList.remove('active');
}

function showToast(msg) {
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = 'toast';
  toast.textContent = msg;
  container.appendChild(toast);
  setTimeout(() => {
    toast.remove();
  }, 3500);
}

function confirmReceivedObject(lostId) {
  const item = appState.data.lostItems.find(i => i.id === lostId);
  if (!item) return;

  const confirmed = confirm(
    `🎉 Great news!\n\nConfirm that you have received your "${item.itemName}" back from campus custody or the finder.\n\nThis will mark the item as recovered and immediately remove it from the active campus lost devices list.`
  );

  if (confirmed) {
    item.status = 'Resolved';
    // Remove any suggested matches for this item
    appState.data.matches = appState.data.matches.filter(m => m.lostId !== lostId);

    // Add celebratory notification
    appState.data.notifications.unshift({
      id: 'N_' + Date.now(),
      userId: item.userId,
      title: '🎉 Device/Object Recovered!',
      message: `Your lost report for "${item.itemName}" was marked as resolved and removed from the active campus lost devices catalog.`,
      date: new Date().toISOString().split('T')[0],
      read: false
    });

    appState.saveState();
    closeModal('modal-detail');
    showToast(`🎉 "${item.itemName}" marked as recovered and removed from the lost list!`);
    renderAll();
  }
}

function escapeHTML(str) {
  if (!str) return '';
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
}
