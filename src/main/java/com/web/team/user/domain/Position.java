package com.web.team.user.domain;

public enum Position {

    INTURN("인턴"),
    STAFF("사원"),
    ASSISTANT_MANAGER("대리"),
    MANAGER("과장"),
    SENIOR_MANAGER("차장"),
    DIRECTOR("부장"),
    TEAM_LEAD("팀장"),
    EXECUTIVE("임원");

    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
