CREATE TABLE IF NOT EXISTS admin (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  role ENUM('ADMIN', 'USER') NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX UK_admin_username (username)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  role ENUM('ADMIN', 'USER') NOT NULL,
  position ENUM('INTURN','STAFF','ASSISTANT_MANAGER','MANAGER','SENIOR_MANAGER','DIRECTOR','TEAM_LEAD','EXECUTIVE') NOT NULL,
  is_active BIT(1) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE INDEX UK_users_email (email)
);

CREATE TABLE IF NOT EXISTS board (
  id BIGINT NOT NULL AUTO_INCREMENT,
  uuid VARCHAR(255) NOT NULL,
  title VARCHAR(255),
  content TEXT,
  created_at DATETIME(6),
  admin_id BIGINT,
  board_status ENUM('PUBLIC','PRIVATE'),
  PRIMARY KEY (id),
  UNIQUE INDEX UK_board_uuid (uuid),
  UNIQUE INDEX UK_board_title (title),
  INDEX FK_board_admin (admin_id),
  CONSTRAINT FK_board_admin FOREIGN KEY (admin_id) REFERENCES admin (id)
);

CREATE TABLE IF NOT EXISTS board_image (
  id BIGINT NOT NULL AUTO_INCREMENT,
  original_filename VARCHAR(255),
  content_type VARCHAR(255),
  data LONGBLOB NOT NULL,
  board_id BIGINT,
  PRIMARY KEY (id),
  INDEX FK_image_board (board_id),
  CONSTRAINT FK_image_board FOREIGN KEY (board_id) REFERENCES board (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_room (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  room_type ENUM('DIRECT','GROUP'),
  user_count INT NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS chat_participant (
  id BIGINT NOT NULL AUTO_INCREMENT,
  chat_room_id BIGINT,
  user_id BIGINT,
  exited BIT(1) NOT NULL,
  entered_at DATETIME(6),
  exited_at DATETIME(6),
  PRIMARY KEY (id),
  INDEX FK_participant_room (chat_room_id),
  INDEX FK_participant_user (user_id),
  CONSTRAINT FK_participant_room FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
  CONSTRAINT FK_participant_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS chat_message (
  id BIGINT NOT NULL AUTO_INCREMENT,
  chat_room_id BIGINT,
  sender_id BIGINT,
  message TEXT,
  sent_at DATETIME(6),
  PRIMARY KEY (id),
  INDEX FK_message_room (chat_room_id),
  INDEX FK_message_sender (sender_id),
  CONSTRAINT FK_message_room FOREIGN KEY (chat_room_id) REFERENCES chat_room (id) ON DELETE CASCADE,
  CONSTRAINT FK_message_sender FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS schedule (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255),
  content TEXT,
  started_at DATETIME(6),
  end_at DATETIME(6),
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  is_company BIT(1),
  type ENUM('COMPANY','BIRTH','ETC','PROMISE','FAMILY'),
  admin_id BIGINT,
  user_id BIGINT,
  PRIMARY KEY (id),
  INDEX FK_schedule_admin (admin_id),
  INDEX FK_schedule_user (user_id),
  CONSTRAINT FK_schedule_admin FOREIGN KEY (admin_id) REFERENCES admin (id),
  CONSTRAINT FK_schedule_user FOREIGN KEY (user_id) REFERENCES users (id)
);
