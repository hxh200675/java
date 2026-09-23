<template>
  <div class="scene">
    <div class="beam beam--1"></div>
    <div class="beam beam--2"></div>
    <div class="beam beam--3"></div>
    <div class="grain"></div>
    <div class="vignette"></div>
  </div>
</template>

<script setup></script>

<style scoped>
.scene {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  background: radial-gradient(
    120% 80% at 50% -10%,
    #2a1c3d 0%,
    #150f1f 45%,
    #0b0710 100%
  );
}

.beam {
  position: absolute;
  top: -12%;
  left: 50%;
  width: 42vw;
  height: 135vh;
  transform-origin: top center;
  background: linear-gradient(
    to bottom,
    rgba(245, 197, 66, 0.2),
    rgba(245, 197, 66, 0) 70%
  );
  filter: blur(16px);
  mix-blend-mode: screen;
  animation: flicker 6s ease-in-out infinite alternate;
}

.beam--1 {
  transform: translateX(-50%) rotate(-24deg);
  opacity: 0.7;
}

.beam--2 {
  width: 26vw;
  transform: translateX(-50%) rotate(2deg);
  opacity: 0.5;
  animation-duration: 8s;
}

.beam--3 {
  transform: translateX(-50%) rotate(26deg);
  opacity: 0.62;
  animation-duration: 10s;
}

@keyframes flicker {
  0% {
    opacity: 0.72;
  }
  50% {
    opacity: 0.42;
  }
  100% {
    opacity: 0.6;
  }
}

.grain {
  position: absolute;
  inset: 0;
  opacity: 0.07;
  mix-blend-mode: overlay;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='140' height='140'%3E%3Cfilter id='n'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='2' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23n)'/%3E%3C/svg%3E");
}

.vignette {
  position: absolute;
  inset: 0;
  background: radial-gradient(
    120% 120% at 50% 40%,
    transparent 55%,
    rgba(0, 0, 0, 0.72) 100%
  );
}
</style>
