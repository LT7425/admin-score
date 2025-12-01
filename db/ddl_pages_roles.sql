-- RBAC schema for pages and roles
-- Database: score_admin

-- pages: store frontend pages/routes
CREATE TABLE IF NOT EXISTS pages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(128) NOT NULL UNIQUE,
  path VARCHAR(255),
  component VARCHAR(255),
  title VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- roles: store role definitions
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- users_roles: many-to-many between users and roles
CREATE TABLE IF NOT EXISTS users_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- roles_pages: many-to-many between roles and pages
CREATE TABLE IF NOT EXISTS roles_pages (
  role_id BIGINT NOT NULL,
  page_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, page_id),
  CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_rp_page FOREIGN KEY (page_id) REFERENCES pages(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- seed roles
INSERT INTO roles (code, name, description) VALUES
('ADMIN','管理员','拥有全部页面权限'),
('USER','用户','普通用户页面权限')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description);

-- seed pages from current project (static and backend dynamic)
INSERT INTO pages (name, path, component, title) VALUES
('Home','/','layout/index.vue','首页'),
('Welcome','/welcome','views/welcome/index.vue','首页'),
('Login','/login','views/login/index.vue','登录'),
('Redirect','/redirect/:path(.*)','layout/redirect.vue','重定向'),
('403','/error/403','views/error/403.vue','403'),
('404','/error/404','views/error/404.vue','404'),
('500','/error/500','views/error/500.vue','500'),
('HomeAsync','/home','dashboard/workbench/index','首页（动态）'),
('TeamManagement','/permission/team','permission/team/index','队伍管理'),
('UserProfile','/profile','profile/index','个人资料')
ON DUPLICATE KEY UPDATE path=VALUES(path), component=VALUES(component), title=VALUES(title);

-- grant ADMIN all pages
INSERT INTO roles_pages (role_id, page_id)
SELECT r.id, p.id FROM roles r JOIN pages p WHERE r.code='ADMIN'
ON DUPLICATE KEY UPDATE role_id=role_id;

-- grant USER selected pages
INSERT INTO roles_pages (role_id, page_id)
SELECT r.id, p.id FROM roles r JOIN pages p ON r.code='USER' AND p.name IN ('Home','Welcome','UserProfile','Login')
ON DUPLICATE KEY UPDATE role_id=role_id;

-- notes:
-- 1) Assign users to roles by inserting into users_roles, e.g. the default admin user:
--    INSERT INTO users_roles (user_id, role_id)
--    SELECT u.id, r.id FROM users u JOIN roles r WHERE u.username='admin' AND r.code='ADMIN';
-- 2) If you add new pages in frontend or backend, insert them into pages and map via roles_pages.
-- 3) Ensure your database is score_admin and MySQL user has permissions.
