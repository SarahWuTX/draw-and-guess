import anime from "animejs";

export function translate(element) {
  anime({
    targets: element,
    translateX: 500
  });
}
