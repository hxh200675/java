// 幕幕光影 · 登录交互
(function () {
  const form = document.getElementById('loginForm');
  const account = document.getElementById('account');
  const password = document.getElementById('password');
  const togglePwd = document.getElementById('togglePwd');
  const toast = document.getElementById('toast');

  let toastTimer;
  function showToast(msg, type) {
    clearTimeout(toastTimer);
    toast.textContent = msg;
    toast.className = 'toast show ' + (type || '');
    toastTimer = setTimeout(() => (toast.className = 'toast'), 2600);
  }

  function setErr(name, msg) {
    const el = document.querySelector(`.field__err[data-for="${name}"]`);
    if (el) el.textContent = msg || '';
  }

  // 密码可见切换
  togglePwd.addEventListener('click', () => {
    const show = password.type === 'password';
    password.type = show ? 'text' : 'password';
    togglePwd.setAttribute('aria-label', show ? '隐藏密码' : '显示密码');
  });

  // 简单校验
  function validate() {
    let ok = true;
    const acc = account.value.trim();
    const pwd = password.value;
    if (!acc) { setErr('account', '请输入账号'); ok = false; }
    else if (!/^(1\d{10}|[\w.+-]+@[\w-]+\.[\w.-]+)$/.test(acc)) { setErr('account', '手机号或邮箱格式不正确'); ok = false; }
    else setErr('account', '');
    if (!pwd) { setErr('password', '请输入密码'); ok = false; }
    else if (pwd.length < 6) { setErr('password', '密码至少 6 位'); ok = false; }
    else setErr('password', '');
    return ok;
  }

  account.addEventListener('input', () => setErr('account', ''));
  password.addEventListener('input', () => setErr('password', ''));

  form.addEventListener('submit', (e) => {
    e.preventDefault();
    if (!validate()) { showToast('请检查表单填写', 'err'); return; }
    const btn = form.querySelector('.enter');
    btn.disabled = true;
    btn.style.opacity = '.7';
    showToast('检票中…', '');
    setTimeout(() => {
      btn.disabled = false;
      btn.style.opacity = '1';
      showToast(`欢迎回来，${account.value.trim()} 🎬`, 'ok');
    }, 1100);
  });

  // 演示按钮
  document.querySelectorAll('.social, #forgot, #signup').forEach((b) =>
    b.addEventListener('click', (e) => {
      e.preventDefault();
      showToast('演示页面，该功能未接入后端', '');
    })
  );
})();
