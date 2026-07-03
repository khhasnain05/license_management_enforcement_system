/* ═══════════════════════════════════════════════════════════
   DLMS — Global JavaScript Utilities
   Shared across all pages. Handles:
   - REST API calls to Spring Boot backend
   - Session / auth helpers
   - Toast notifications
   - Loading states
   - Shared UI interactions
   ═══════════════════════════════════════════════════════════ */

'use strict';

/* ── BASE API ─────────────────────────────────────────────── */
const API_BASE = '';   // same-origin; change to 'http://localhost:8080' for dev cross-origin

const API = {
  // 1. The Bulletproof Request Engine
  _req(method, url, body) {
    const opts = {
      method,
      credentials: 'same-origin',
      headers: { 'Content-Type': 'application/json' }
    };
    if (body) opts.body = JSON.stringify(body);
    
    return fetch(API_BASE + url, opts).then(async r => {
      // If not logged in, redirect to login page
      if (r.status === 401) { window.location.href = '/login'; return; }
      
      // SAFETY NET: Check if Spring Boot actually sent JSON!
      const contentType = r.headers.get("content-type");
      if (contentType && contentType.indexOf("application/json") !== -1) {
        return r.json();
      } else {
        // If Spring Boot sent a 404 HTML page, safely ignore it without crashing
        console.warn(`[DLMS Warning] Endpoint ${url} is missing or returned HTML.`);
        return { success: false, data: null };
      }
    }).catch(err => {
      console.error("Fetch error:", err);
      return { success: false, data: null };
    });
  },

  // 2. The Missing Functions (This is what caused your error!)
  get:    (url)        => API._req('GET',    url),
  post:   (url, body)  => API._req('POST',   url, body),
  put:    (url, body)  => API._req('PUT',    url, body),
  delete: (url)        => API._req('DELETE', url),

  // 3. Document Upload Function
  upload(url, formData) {
    return fetch(API_BASE + url, {
      method: 'POST',
      credentials: 'same-origin',
      body: formData   // No Content-Type header — browser sets boundary automatically
    }).then(r => r.json());
  }
};

/* ── TOAST NOTIFICATIONS ─────────────────────────────────── */
const Toast = (() => {
  let container;
  function init() {
    if (container) return;
    container = document.createElement('div');
    container.style.cssText = `
      position:fixed; bottom:24px; right:24px; z-index:9999;
      display:flex; flex-direction:column; gap:10px; pointer-events:none;
    `;
    document.body.appendChild(container);
  }

  function show(message, type = 'info', duration = 4000) {
    init();
    const colors = {
      success: { bg: 'rgba(34,197,94,0.15)', border: 'rgba(34,197,94,0.3)', icon: '✓', color: '#22c55e' },
      error:   { bg: 'rgba(255,77,109,0.15)', border: 'rgba(255,77,109,0.3)', icon: '✕', color: '#ff4d6d' },
      info:    { bg: 'rgba(0,212,170,0.1)',   border: 'rgba(0,212,170,0.2)',  icon: 'ℹ', color: '#00d4aa' },
      warning: { bg: 'rgba(245,158,11,0.12)', border: 'rgba(245,158,11,0.25)', icon: '!', color: '#f59e0b' }
    };
    const c = colors[type] || colors.info;
    const toast = document.createElement('div');
    toast.style.cssText = `
      background:${c.bg}; border:1px solid ${c.border};
      backdrop-filter:blur(16px);
      border-radius:12px; padding:12px 16px;
      display:flex; align-items:center; gap:10px;
      font-family:'Sora',sans-serif; font-size:0.85rem;
      color:#e8edf5; pointer-events:all;
      transform:translateX(120%); transition:transform 0.3s cubic-bezier(0.4,0,0.2,1);
      max-width:320px; box-shadow:0 4px 24px rgba(0,0,0,0.4);
    `;
    toast.innerHTML = `
      <span style="width:22px;height:22px;border-radius:50%;background:${c.color}22;
        display:flex;align-items:center;justify-content:center;
        font-size:0.7rem;font-weight:700;color:${c.color};flex-shrink:0;">${c.icon}</span>
      <span>${message}</span>
    `;
    container.appendChild(toast);
    requestAnimationFrame(() => toast.style.transform = 'translateX(0)');
    setTimeout(() => {
      toast.style.transform = 'translateX(120%)';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  }

  return { success: m => show(m,'success'), error: m => show(m,'error'),
           info: m => show(m,'info'), warning: m => show(m,'warning') };
})();

/* ── LOADING OVERLAY ─────────────────────────────────────── */
const Loader = {
  _el: null,
  show(text = 'Loading...') {
    if (this._el) return;
    this._el = document.createElement('div');
    this._el.style.cssText = `
      position:fixed;inset:0;background:rgba(10,15,30,0.85);
      backdrop-filter:blur(8px);z-index:9998;
      display:flex;flex-direction:column;align-items:center;justify-content:center;gap:16px;
    `;
    this._el.innerHTML = `
      <div style="width:42px;height:42px;border-radius:50%;
        border:3px solid rgba(0,212,170,0.2);border-top-color:#00d4aa;
        animation:spin 0.8s linear infinite;"></div>
      <span style="font-family:'Sora',sans-serif;color:#8892aa;font-size:0.88rem;">${text}</span>
    `;
    if (!document.getElementById('dlms-spin-style')) {
      const s = document.createElement('style');
      s.id = 'dlms-spin-style';
      s.textContent = '@keyframes spin{to{transform:rotate(360deg)}}';
      document.head.appendChild(s);
    }
    document.body.appendChild(this._el);
  },
  hide() { if (this._el) { this._el.remove(); this._el = null; } }
};

/* ── SESSION HELPERS ─────────────────────────────────────── */
const Session = {
  get user()     { try { return JSON.parse(sessionStorage.getItem('dlms_user') || 'null'); } catch { return null; } },
  set user(val)  { sessionStorage.setItem('dlms_user', JSON.stringify(val)); },
  clear()        { sessionStorage.clear(); },
  requireAuth()  { if (!this.user) window.location.href = '/login'; },
  requireRole(r) { if (this.user?.role !== r) window.location.href = '/login'; }
};

/* ── MODAL HELPERS ───────────────────────────────────────── */
function openModal(id)  { document.getElementById(id)?.classList.add('open'); }
function closeModal(id) { document.getElementById(id)?.classList.remove('open'); }

/* Close modals on overlay click */
document.addEventListener('click', e => {
  if (e.target.classList.contains('modal-overlay')) {
    e.target.classList.remove('open');
  }
});

/* ── SIDEBAR TOGGLE (mobile) ─────────────────────────────── */
function toggleSidebar() {
  document.querySelector('.sidebar')?.classList.toggle('open');
}

/* ── NAV ACTIVE STATE ────────────────────────────────────── */
function setActiveNav(id) {
  document.querySelectorAll('.nav-item').forEach(el => el.classList.remove('active'));
  document.getElementById(id)?.classList.add('active');
}

/* ── LOGOUT ──────────────────────────────────────────────── */
function logout() {
  API.post('/api/auth/logout').finally(() => {
    Session.clear();
    window.location.href = '/login';
  });
}

/* ── FORMAT HELPERS ──────────────────────────────────────── */
const fmt = {
  date(d) {
    if (!d) return '—';
    return new Date(d).toLocaleDateString('en-PK', { day:'2-digit', month:'short', year:'numeric' });
  },
  dateTime(d) {
    if (!d) return '—';
    return new Date(d).toLocaleString('en-PK', { day:'2-digit', month:'short', year:'numeric', hour:'2-digit', minute:'2-digit' });
  },
  currency(n) { return 'PKR ' + Number(n).toLocaleString('en-PK'); },
  initials(name) {
    if (!name) return 'U';
    return name.split(' ').slice(0,2).map(w => w[0]).join('').toUpperCase();
  },
  statusBadge(status) {
    const map = {
      PENDING:      'badge-amber',
      UNDER_REVIEW: 'badge-gold',
      APPROVED:     'badge-teal',
      REJECTED:     'badge-red',
      ACTIVE:       'badge-green',
      EXPIRED:      'badge-muted',
      PASSED:       'badge-teal',
      FAILED:       'badge-red',
      SCHEDULED:    'badge-gold',
      PAID:         'badge-green',
    };
    return `<span class="badge ${map[status]||'badge-muted'}">${status}</span>`;
  }
};

/* ── RIPPLE EFFECT ───────────────────────────────────────── */
document.addEventListener('click', e => {
  const btn = e.target.closest('.btn');
  if (!btn) return;
  const r = document.createElement('span');
  const rect = btn.getBoundingClientRect();
  r.style.cssText = `
    position:absolute;border-radius:50%;
    width:6px;height:6px;
    background:rgba(255,255,255,0.4);
    transform:scale(0);
    animation:ripple 0.5s ease;
    pointer-events:none;
    left:${e.clientX-rect.left-3}px;
    top:${e.clientY-rect.top-3}px;
  `;
  if (getComputedStyle(btn).position === 'static') btn.style.position = 'relative';
  btn.appendChild(r);
  r.addEventListener('animationend', () => r.remove());
});
const rStyle = document.createElement('style');
rStyle.textContent = '@keyframes ripple{to{transform:scale(40);opacity:0}}';
document.head.appendChild(rStyle);

/* ── ANIMATE ON LOAD ─────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.fade-in, .stagger > *').forEach((el, i) => {
    el.style.opacity = '0';
    el.style.animation = `fadeIn 0.4s ease ${i * 0.05}s both`;
  });
});
