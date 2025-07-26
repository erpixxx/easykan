import { Card, type CardProps } from "./Card.tsx";
import { ButtonIcon } from "./ButtonIcon.tsx";
import {type CSSProperties} from "react";
import {parseToRgb} from "polished";
import {Draggable, type DraggableProvidedDragHandleProps, Droppable} from "@hello-pangea/dnd";

export interface ColumnProps {
  id: string,
  title: string,
  color: string,
  cards: CardProps[],
  dragHandleProps: DraggableProvidedDragHandleProps | null
}

export function Column({id, title, color = "#326396", cards = [], dragHandleProps }: ColumnProps) {
  const rgb = parseToRgb(color);
  const style = {
    "--column-color-base": `${rgb.red}, ${rgb.green}, ${rgb.blue}`,
    // "--column-color-text": getLuminance(color) > 0.4 ? COLORS['dark'] : COLORS['light'],
    // "--column-alpha": getLuminance(color) > 0.4 ? 'var(--column-dark-alpha)' : 'var(--column-light-alpha)'
  } as CSSProperties;

  return (
    <div className="column" style={style}>
      <div className="column-title" {...dragHandleProps}>
        <h3>{title}</h3>
      </div>
      <Droppable droppableId={id}>
        {(provided) => (
          <div
            className="column-cards scrollable"
            ref={provided.innerRef}
            {...provided.droppableProps}
          >
            {cards.map((card, index) => (
              <Draggable key={card.id} draggableId={card.id} index={index}>
                {(provided, snapshot) => (
                  <div
                    className={`column-card ${snapshot.isDragging ? 'dragging' : ''}`}
                    ref={provided.innerRef}
                    {...provided.draggableProps}
                    {...provided.dragHandleProps}
                  >
                    <Card {...card} />
                  </div>
                )}
              </Draggable>
            ))}
            {provided.placeholder}
          </div>
        )}
      </Droppable>
      <div className="column-footer">
        <ButtonIcon icon="add" label="Add new card" className="column-add-new-card" />
      </div>
    </div>
  );
}