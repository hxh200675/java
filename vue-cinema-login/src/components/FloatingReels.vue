<template>
  <div class="reels" aria-hidden="true">
    <span class="cell"></span>
    <span class="cell"></span>
    <span class="cell"></span>
    <span class="cell"></span>
    <span class="cell"></span>
    <span class="cell"></span>
  </div>
</template>

<script setup></script>

<style scoped>
.reels {
  position: fixed;
  top: 9%;
  left: 0;
  right: 0;
  z-index: 1;
  display: flex;
  gap: 14px;
  justify-content: center;
  opacity: 0.25;
  filter: blur(0.3px);
  pointer-events: none;
  animation: drift 18s ease-in-out infinite alternate;
}

.cell {
  width: 56px;
  height: 40px;
  border-radius: 6px;
  background: linear-gradient(135deg, #f5c542, #b8862b);
  box-shadow: inset 0 0 0 4px rgba(0, 0, 0, 0.35),
    0 6px 18px rgba(0, 0, 0, 0.4);
  position: relative;
}

.cell::before,
.cell::after {
  content: "";
  position: absolute;
  left: 0;
  right: 0;
  height: 6px;
  background-image: radial-gradient(circle, #0b0710 2px, transparent 2.6px);
  background-size: 10px 6px;
}

.cell::before {
  top: -2px;
}

.cell::after {
  bottom: -2px;
}

@keyframes drift {
  from {
    transform: translateX(-4%);
  }
  to {
    transform: translateX(4%);
  }
}
</style>
