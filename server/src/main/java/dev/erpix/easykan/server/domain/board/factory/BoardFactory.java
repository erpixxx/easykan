package dev.erpix.easykan.server.domain.board.factory;

import dev.erpix.easykan.server.domain.board.model.Board;
import dev.erpix.easykan.server.domain.project.model.Project;
import dev.erpix.easykan.server.domain.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class BoardFactory {

	public Board create(Project project, User user, String name, int nextPosition) {
		return Board.builder().project(project).owner(user).name(name).position(nextPosition).build();
	}

}
