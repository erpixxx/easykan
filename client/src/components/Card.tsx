import {CardTag, type CardTagProps} from "./CardTag.tsx";

export interface CardProps {
  id: string;
  title: string;
  tags: CardTagProps[];
  progress?: number;
}

export function Card({ title, tags, progress }: CardProps) {
  return (
    <div className="card">
      <div className="card-header">
        <div className="card-tags">
          {tags.map((tag, index) => (
            <CardTag {...tag} key={index} />
          ))}
        </div>
        <div className="card-title">
          <h3>{title}</h3>
        </div>
      </div>
      {
        progress !== undefined && (
          <div className="card-progress">
            <div className="progress-bar">
              <div className="progress" style={{"width": progress + '%'}}></div>
            </div>
            <div className="progress-percentage">
              <span className="percentage">{progress}%</span>
            </div>
          </div>
        )
      }
      <div className="card-image">
        <img className="card-image--img" src="https://placehold.co/1000x200" alt="Card" />
      </div>
      <div className="card-footer">
        <div className="card-time">
          <span className="time">2 days ago</span>
        </div>
        <div className="card-members">
          <img className="member" src="https://picsum.photos/128/128" alt="member" />
          <img className="member" src="https://picsum.photos/128/128" alt="member" />
        </div>
      </div>
    </div>
  );
}