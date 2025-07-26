import {getLuminance, mix} from "polished";
import type { CSSProperties } from "react";

export type CardTagProps = {
  name: string,
  color?: string
}

export function CardTag({ name, color = "white" }: CardTagProps) {
  const luminance = getLuminance(color);

  // I hate it I hate it I hate it
  // I ain't front-end developer, there must be a better way to do this
  // For now we use a simple heuristic to determine how to mix the color

  // The brighter the color, the more we darken it
  const mixRatio = luminance > 0.6
    ? 0.4 // darken bright colors
    : luminance < 0.3
      ? 0.5 // lighten dark colors
      : 0.5; // default mix ratio

  const bg = luminance > 0.2
    ? mix(mixRatio, "#000", color)
    : mix(mixRatio, "#fff", color);

  const style = {
    "--tag-color": color,
    "--tag-bg": bg,
  } as CSSProperties;

  return (
    <span className="card-tag" style={style}>
      {name}
    </span>
  );
}