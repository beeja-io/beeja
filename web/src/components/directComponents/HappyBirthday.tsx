import React, { useEffect, useRef } from 'react';

const HappyBirthday: React.FC = () => {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const birthday = useRef<Birthday | null>(null);
  const then = useRef<number>(new Date().getTime());

  const PI2 = Math.PI * 2;
  const random = (min: number, max: number): number =>
    (Math.random() * (max - min + 1) + min) | 0;

  class Birthday {
    fireworks: Firework[] = [];
    counter = 0;
    width: number = 0;
    height: number = 0;
    spawnA: number = 0;
    spawnB: number = 0;
    spawnC: number = 0;
    spawnD: number = 0;

    constructor() {
      this.resize();
    }

    resize() {
      const canvas = canvasRef.current;
      if (!canvas) return;

      this.width = canvas.width = window.innerWidth;
      const center = (this.width / 2) | 0;
      this.spawnA = (center - center / 4) | 0;
      this.spawnB = (center + center / 4) | 0;

      this.height = canvas.height = window.innerHeight;
      this.spawnC = this.height * 0.1;
      this.spawnD = this.height * 0.5;
    }

    onClick(evt: MouseEvent | TouchEvent) {
      const canvas = canvasRef.current;
      if (!canvas) return;

      const isTouch = (e: MouseEvent | TouchEvent): e is TouchEvent =>
        'touches' in e;
      const x = isTouch(evt)
        ? evt.touches[0].pageX
        : (evt as MouseEvent).clientX;
      const y = isTouch(evt)
        ? evt.touches[0].pageY
        : (evt as MouseEvent).clientY;

      const count = random(3, 5);
      for (let i = 0; i < count; i++) {
        this.fireworks.push(
          new Firework(
            random(this.spawnA, this.spawnB),
            this.height,
            x,
            y,
            random(0, 260),
            random(30, 110)
          )
        );
      }
      this.counter = -1;
    }

    update(delta: number) {
      const canvas = canvasRef.current;
      if (!canvas) return;
      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      // Set the background to transparent
      ctx.globalCompositeOperation = 'destination-out';
      ctx.fillStyle = `rgba(0, 0, 0, ${7 * delta})`; // Transparent background
      ctx.fillRect(0, 0, this.width, this.height);

      ctx.globalCompositeOperation = 'lighter';
      for (const firework of this.fireworks) firework.update(delta);

      this.counter += delta * 3;
      if (this.counter >= 1) {
        this.fireworks.push(
          new Firework(
            random(this.spawnA, this.spawnB),
            this.height,
            random(0, this.width),
            random(this.spawnC, this.spawnD),
            random(0, 360),
            random(30, 110)
          )
        );
        this.counter = 0;
      }

      if (this.fireworks.length > 1000) {
        this.fireworks = this.fireworks.filter((firework) => !firework.dead);
      }
    }
  }

  class Firework {
    x: number;
    y: number;
    targetX: number;
    targetY: number;
    shade: number;
    offsprings: number;
    dead = false;
    history: { x: number; y: number }[] = [];
    madeChilds = false;

    constructor(
      x: number,
      y: number,
      targetX: number,
      targetY: number,
      shade: number,
      offsprings: number
    ) {
      this.x = x;
      this.y = y;
      this.targetX = targetX;
      this.targetY = targetY;
      this.shade = shade;
      this.offsprings = offsprings;
    }

    update(delta: number) {
      const canvas = canvasRef.current;
      if (!canvas) return;
      const ctx = canvas.getContext('2d');
      if (!ctx) return;

      if (this.dead) return;

      const xDiff = this.targetX - this.x;
      const yDiff = this.targetY - this.y;
      if (Math.abs(xDiff) > 3 || Math.abs(yDiff) > 3) {
        this.x += xDiff * 2 * delta;
        this.y += yDiff * 2 * delta;

        this.history.push({ x: this.x, y: this.y });

        if (this.history.length > 20) this.history.shift();
      } else {
        if (this.offsprings && !this.madeChilds) {
          const babies = this.offsprings / 2;
          for (let i = 0; i < babies; i++) {
            const targetX =
              (this.x + this.offsprings * Math.cos((PI2 * i) / babies)) | 0;
            const targetY =
              (this.y + this.offsprings * Math.sin((PI2 * i) / babies)) | 0;
            birthday.current?.fireworks.push(
              new Firework(this.x, this.y, targetX, targetY, this.shade, 0)
            );
          }
        }
        this.madeChilds = true;
        this.history.shift();
      }

      if (this.history.length === 0) this.dead = true;
      else if (this.offsprings) {
        for (let i = 0; i < this.history.length; i++) {
          const point = this.history[i];
          ctx.beginPath();
          ctx.fillStyle = `hsl(${this.shade},100%,${i}%)`;
          ctx.arc(point.x, point.y, 1, 0, PI2, false);
          ctx.fill();
        }
      } else {
        ctx.beginPath();
        ctx.fillStyle = `hsl(${this.shade},100%,50%)`;
        ctx.arc(this.x, this.y, 1, 0, PI2, false);
        ctx.fill();
      }
    }
  }

  useEffect(() => {
    const canvas = canvasRef.current;
    const ctx = canvas?.getContext('2d');
    if (ctx && canvas) {
      birthday.current = new Birthday();

      const handleResize = () => birthday.current?.resize();
      const handleClick = (evt: MouseEvent | TouchEvent) =>
        birthday.current?.onClick(evt);

      window.addEventListener('resize', handleResize);
      document.addEventListener('click', handleClick);
      document.addEventListener('touchstart', handleClick);

      const loop = () => {
        requestAnimationFrame(loop);
        const now = new Date().getTime();
        const delta = now - then.current;
        then.current = now;
        birthday.current?.update(delta / 1000);
      };

      loop();

      return () => {
        window.removeEventListener('resize', handleResize);
        document.removeEventListener('click', handleClick);
        document.removeEventListener('touchstart', handleClick);
      };
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <>
      <h1 className="eventText glitch">Happy Birthday &#127881;</h1>
      <canvas ref={canvasRef} id="birthday"></canvas>
    </>
  );
};

export default HappyBirthday;
