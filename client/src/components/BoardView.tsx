import {Column, type ColumnProps} from "./Column.tsx";
import {useState} from "react";
import {DragDropContext, Draggable, Droppable, type DropResult} from "@hello-pangea/dnd";
import type {CardProps} from "./Card.tsx";

export function BoardView() {
  const [columns, setColumns] = useState<Record<string, ColumnProps>>({
    "column-1": {
      id: "column-1",
      title: "Dropped",
      color: "#d25656",
      cards: [
        { id: "1", title: "Card 1",
          tags: [
            {name: "Bug", color: "#d94b4b"},
            {name: "tag2", color: "#8fd9b2"}
          ], progress: 50
        },
        { id: "2", title: "Card 2",
          tags: [
            {name: "tag3", color: "#e385b4"},
            {name: "tag4", color: "#9a66dc"}
          ], progress: 12
        },
        { id: "3", title: "Card 3",
          tags: [
            {name: "tag5", color: "#6396dc"},
            {name: "tag6", color: "#5ecdd9"}
          ], progress: 97
        },
      ],
      dragHandleProps: null // No drag handle for this column
    },
    "column-2": {
      id: "column-2",
      title: "Completed",
      color: "#28945a",
      cards: [
        { id: "4", title: "Base Spring setup",
          tags: [
            {name: "tag7", color: "#bba9a9"},
            {name: "tag8", color: "#e8ffa3"}
          ], progress: 100 },
        { id: "5", title: "Back-end endpoints",
          tags: [
            {name: "tag9", color: "#28945a"},
            {name: "tag10", color: "#eaff02"}
          ], progress: 57 },
      ],
      dragHandleProps: null // No drag handle for this column
    },
    "column-3": {
      id: "column-3",
      title: "To Do",
      color: "#4e93d0",
      cards: [
        { id: "6", title: "Card 6",
          tags: [
            {name: "tag11", color: "#81ff72"},
            {name: "tag12", color: "#0e8000"}
          ] },
        { id: "7", title: "Card 7",
          tags: [
            {name: "tag13", color: "#629dea"},
            {name: "tag14", color: "#074b09"}
          ], progress: 0 },
      ],
      dragHandleProps: null // No drag handle for this column
    }
  });

  function onDragEnd(result: DropResult) {
    const { source, destination, type } = result;

    if (!destination) {
      // Dropped outside a droppable area
      return;
    }

    if (source.index === destination.index && source.droppableId === destination.droppableId) {
      // Dropped in the same position
      return;
    }

    if (type === "COLUMN") {
      const colEntries = Object.entries(columns);
      const [removed] = colEntries.splice(source.index, 1);
      colEntries.splice(destination.index, 0, removed);

      const newColumns = Object.fromEntries(colEntries);
      setColumns(newColumns);
      return;
    }

    const sourceCol = columns[source.droppableId];
    const destCol = columns[destination.droppableId];

    if (!sourceCol || !destCol) return;

    const sourceCards: CardProps[] = [...sourceCol.cards];
    const destCards: CardProps[] = [...destCol.cards];

    const [movedCard] = sourceCards.splice(source.index, 1);

    if (source.droppableId === destination.droppableId) {
      // Moving within the same column
      sourceCards.splice(destination.index, 0, movedCard);
      setColumns({
        ...columns,
        [sourceCol.id]: {
          ...sourceCol,
          cards: sourceCards
        }
      });
    }
    else {
      // Moving between columns
      destCards.splice(destination.index, 0, movedCard);
      setColumns({
        ...columns,
        [sourceCol.id]: {
          ...sourceCol,
          cards: sourceCards
        },
        [destCol.id]: {
          ...destCol,
          cards: destCards
        }
      });
    }
  }

  return (
    <main className="board-view">
      <div className="board-view-background" />
      <DragDropContext onDragEnd={onDragEnd}>
        <Droppable droppableId="board" direction="horizontal" type="COLUMN">
          {provided => (
            <div
              className="board-view-columns"
              ref={provided.innerRef}
              {...provided.droppableProps}
            >
              {Object.values(columns).map((col, index) => (
                <Draggable draggableId={col.id} index={index } key={col.id}>
                  {provided => (
                    <div
                      className="column-wrapper"
                      ref={provided.innerRef}
                      {...provided.draggableProps}
                    >
                      <Column {...col} dragHandleProps={provided.dragHandleProps} />
                    </div>
                  )}
                </Draggable>
              ))}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </main>
  );
}
