-- Nếu database chưa tồn tại, chạy riêng: CREATE DATABASE [ShopFlow];
-- Câu USE bên dưới chọn database dùng chung trước khi tạo các bảng.
USE [ShopFlow];

IF SCHEMA_ID(N'ShopFlow') IS NULL
BEGIN
    EXEC(N'CREATE SCHEMA [ShopFlow]');
END;

SET ANSI_NULLS ON;
SET ANSI_PADDING ON;
SET ANSI_WARNINGS ON;
SET ARITHABORT ON;
SET CONCAT_NULL_YIELDS_NULL ON;
SET QUOTED_IDENTIFIER ON;
SET NUMERIC_ROUNDABORT OFF;

IF OBJECT_ID(N'ShopFlow.auth_user_account', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_user_account (
                                       id BIGINT IDENTITY(1, 1) NOT NULL,
                                       created_by NVARCHAR(100) NULL,
                                       created_at DATETIME2 NULL,
                                       updated_by NVARCHAR(100) NULL,
                                       updated_at DATETIME2 NULL,
                                       is_deleted INT NOT NULL CONSTRAINT DF_auth_user_account_is_deleted DEFAULT (0),
                                       code NVARCHAR(50) NOT NULL,
                                       username NVARCHAR(100) NOT NULL,
                                       password_hash NVARCHAR(255) NOT NULL,
                                       email NVARCHAR(254) NULL,
                                       account_status NVARCHAR(20) NOT NULL CONSTRAINT DF_auth_user_account_status DEFAULT (N'PENDING'),
                                       failed_login_count INT NOT NULL CONSTRAINT DF_auth_user_account_failed_login_count DEFAULT (0),
                                       locked_until DATETIME2 NULL,
                                       last_login_at DATETIME2 NULL,
                                       password_changed_at DATETIME2 NULL,
                                       CONSTRAINT PK_auth_user_account PRIMARY KEY CLUSTERED (id),
                                       CONSTRAINT UQ_auth_user_account_code UNIQUE (code),
                                       CONSTRAINT UQ_auth_user_account_username UNIQUE (username),
                                       CONSTRAINT CK_auth_user_account_is_deleted CHECK (is_deleted IN (0, 1)),
                                       CONSTRAINT CK_auth_user_account_failed_login_count CHECK (failed_login_count >= 0),
                                       CONSTRAINT CK_auth_user_account_status CHECK (account_status IN (N'PENDING', N'ACTIVE', N'LOCKED', N'DISABLED'))
);
END;

IF OBJECT_ID(N'ShopFlow.auth_user_profile', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_user_profile (
                                       id BIGINT IDENTITY(1, 1) NOT NULL,
                                       created_by NVARCHAR(100) NULL,
                                       created_at DATETIME2 NULL,
                                       updated_by NVARCHAR(100) NULL,
                                       updated_at DATETIME2 NULL,
                                       is_deleted INT NOT NULL CONSTRAINT DF_auth_user_profile_is_deleted DEFAULT (0),
                                       user_code NVARCHAR(50) NOT NULL,
                                       full_name NVARCHAR(200) NOT NULL,
                                       name_search NVARCHAR(255) NULL,
                                       birth_date DATE NULL,
                                       phone NVARCHAR(20) NULL,
                                       avatar_url NVARCHAR(255) NULL,
                                       CONSTRAINT PK_auth_user_profile PRIMARY KEY CLUSTERED (id),
                                       CONSTRAINT FK_auth_user_profile_account FOREIGN KEY (user_code)
                                           REFERENCES ShopFlow.auth_user_account (code),
                                       CONSTRAINT CK_auth_user_profile_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_profile')
      AND name = N'UX_auth_user_profile_user_code_active'
)
BEGIN
CREATE UNIQUE INDEX UX_auth_user_profile_user_code_active
    ON ShopFlow.auth_user_profile (user_code)
    WHERE is_deleted = 0;
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_profile')
      AND name = N'IX_auth_user_profile_name_search'
)
BEGIN
CREATE INDEX IX_auth_user_profile_name_search
    ON ShopFlow.auth_user_profile (name_search);
END;

IF OBJECT_ID(N'ShopFlow.province', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.province (
                              id BIGINT IDENTITY(1, 1) NOT NULL,
                              created_by NVARCHAR(100) NULL,
                              created_at DATETIME2 NULL,
                              updated_by NVARCHAR(100) NULL,
                              updated_at DATETIME2 NULL,
                              is_deleted INT NOT NULL CONSTRAINT DF_province_is_deleted DEFAULT (0),
                              province_name NVARCHAR(100) NOT NULL,
                              CONSTRAINT PK_province PRIMARY KEY CLUSTERED (id),
                              CONSTRAINT CK_province_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF OBJECT_ID(N'ShopFlow.ward', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.ward (
                          id BIGINT IDENTITY(1, 1) NOT NULL,
                          created_by NVARCHAR(100) NULL,
                          created_at DATETIME2 NULL,
                          updated_by NVARCHAR(100) NULL,
                          updated_at DATETIME2 NULL,
                          is_deleted INT NOT NULL CONSTRAINT DF_ward_is_deleted DEFAULT (0),
                          province_id BIGINT NOT NULL,
                          ward_name NVARCHAR(100) NOT NULL,
                          CONSTRAINT PK_ward PRIMARY KEY CLUSTERED (id),
                          CONSTRAINT FK_ward_province FOREIGN KEY (province_id)
                              REFERENCES ShopFlow.province (id),
                          CONSTRAINT CK_ward_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.ward')
      AND name = N'IX_ward_province_id'
)
BEGIN
CREATE INDEX IX_ward_province_id ON ShopFlow.ward (province_id);
END;

IF OBJECT_ID(N'ShopFlow.auth_user_address', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_user_address (
                                       id BIGINT IDENTITY(1, 1) NOT NULL,
                                       created_by NVARCHAR(100) NULL,
                                       created_at DATETIME2 NULL,
                                       updated_by NVARCHAR(100) NULL,
                                       updated_at DATETIME2 NULL,
                                       is_deleted INT NOT NULL CONSTRAINT DF_auth_user_address_is_deleted DEFAULT (0),
                                       user_code NVARCHAR(50) NOT NULL,
                                       address_label NVARCHAR(100) NULL,
                                       recipient_name NVARCHAR(200) NOT NULL,
                                       phone NVARCHAR(20) NOT NULL,
                                       ward_id BIGINT NOT NULL,
                                       ward_name NVARCHAR(255) NOT NULL,
                                       province_id BIGINT NOT NULL,
                                       province_name NVARCHAR(255) NOT NULL,
                                       address_detail NVARCHAR(255) NOT NULL,
                                       note NVARCHAR(255) NULL,
                                       is_default BIT NOT NULL CONSTRAINT DF_auth_user_address_is_default DEFAULT (0),
                                       CONSTRAINT PK_auth_user_address PRIMARY KEY CLUSTERED (id),
                                       CONSTRAINT FK_auth_user_address_account FOREIGN KEY (user_code)
                                           REFERENCES ShopFlow.auth_user_account (code),
                                       CONSTRAINT CK_auth_user_address_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_address')
      AND name = N'IX_auth_user_address_user_default'
)
BEGIN
CREATE INDEX IX_auth_user_address_user_default
    ON ShopFlow.auth_user_address (user_code, is_default);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_address')
      AND name = N'IX_auth_user_address_ward_province'
)
BEGIN
CREATE INDEX IX_auth_user_address_ward_province
    ON ShopFlow.auth_user_address (ward_id, province_id);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_address')
      AND name = N'UX_auth_user_address_one_default'
)
BEGIN
CREATE UNIQUE INDEX UX_auth_user_address_one_default
    ON ShopFlow.auth_user_address (user_code)
    WHERE is_deleted = 0 AND is_default = 1;
END;

IF OBJECT_ID(N'ShopFlow.auth_role', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_role (
                               id BIGINT IDENTITY(1, 1) NOT NULL,
                               created_by NVARCHAR(100) NULL,
                               created_at DATETIME2 NULL,
                               updated_by NVARCHAR(100) NULL,
                               updated_at DATETIME2 NULL,
                               is_deleted INT NOT NULL CONSTRAINT DF_auth_role_is_deleted DEFAULT (0),
                               code NVARCHAR(50) NOT NULL,
                               role_name NVARCHAR(100) NOT NULL,
                               description NVARCHAR(500) NULL,
                               CONSTRAINT PK_auth_role PRIMARY KEY CLUSTERED (id),
                               CONSTRAINT UQ_auth_role_code UNIQUE (code),
                               CONSTRAINT CK_auth_role_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF OBJECT_ID(N'ShopFlow.auth_permission', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_permission (
                                     id BIGINT IDENTITY(1, 1) NOT NULL,
                                     created_by NVARCHAR(100) NULL,
                                     created_at DATETIME2 NULL,
                                     updated_by NVARCHAR(100) NULL,
                                     updated_at DATETIME2 NULL,
                                     is_deleted INT NOT NULL CONSTRAINT DF_auth_permission_is_deleted DEFAULT (0),
                                     code NVARCHAR(50) NOT NULL,
                                     permission_name NVARCHAR(150) NOT NULL,
                                     description NVARCHAR(500) NULL,
                                     CONSTRAINT PK_auth_permission PRIMARY KEY CLUSTERED (id),
                                     CONSTRAINT UQ_auth_permission_code UNIQUE (code),
                                     CONSTRAINT CK_auth_permission_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF OBJECT_ID(N'ShopFlow.auth_menu', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_menu (
                               id BIGINT IDENTITY(1, 1) NOT NULL,
                               created_by NVARCHAR(100) NULL,
                               created_at DATETIME2 NULL,
                               updated_by NVARCHAR(100) NULL,
                               updated_at DATETIME2 NULL,
                               is_deleted INT NOT NULL CONSTRAINT DF_auth_menu_is_deleted DEFAULT (0),
                               code NVARCHAR(50) NOT NULL,
                               parent_code NVARCHAR(50) NULL,
                               menu_name NVARCHAR(150) NOT NULL,
                               route_path NVARCHAR(255) NULL,
                               icon NVARCHAR(100) NULL,
                               sort_order INT NOT NULL CONSTRAINT DF_auth_menu_sort_order DEFAULT (0),
                               menu_status NVARCHAR(20) NOT NULL CONSTRAINT DF_auth_menu_status DEFAULT (N'ACTIVE'),
                               CONSTRAINT PK_auth_menu PRIMARY KEY CLUSTERED (id),
                               CONSTRAINT UQ_auth_menu_code UNIQUE (code),
                               CONSTRAINT FK_auth_menu_parent FOREIGN KEY (parent_code)
                                   REFERENCES ShopFlow.auth_menu (code),
                               CONSTRAINT CK_auth_menu_is_deleted CHECK (is_deleted IN (0, 1)),
                               CONSTRAINT CK_auth_menu_sort_order CHECK (sort_order >= 0),
                               CONSTRAINT CK_auth_menu_status CHECK (menu_status IN (N'ACTIVE', N'DISABLED'))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_menu')
      AND name = N'IX_auth_menu_parent_code'
)
BEGIN
CREATE INDEX IX_auth_menu_parent_code ON ShopFlow.auth_menu (parent_code);
END;

-- Các bảng liên kết thuần không có BaseEntity; thu hồi quyền bằng cách xóa dòng liên kết.
IF OBJECT_ID(N'ShopFlow.auth_user_role', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_user_role (
                                    user_code NVARCHAR(50) NOT NULL,
                                    role_code NVARCHAR(50) NOT NULL,
                                    CONSTRAINT PK_auth_user_role PRIMARY KEY CLUSTERED (user_code, role_code),
                                    CONSTRAINT FK_auth_user_role_account FOREIGN KEY (user_code)
                                        REFERENCES ShopFlow.auth_user_account (code),
                                    CONSTRAINT FK_auth_user_role_role FOREIGN KEY (role_code)
                                        REFERENCES ShopFlow.auth_role (code)
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_user_role')
      AND name = N'IX_auth_user_role_role_code'
)
BEGIN
CREATE INDEX IX_auth_user_role_role_code ON ShopFlow.auth_user_role (role_code);
END;

IF OBJECT_ID(N'ShopFlow.auth_role_permission', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_role_permission (
                                          role_code NVARCHAR(50) NOT NULL,
                                          permission_code NVARCHAR(50) NOT NULL,
                                          CONSTRAINT PK_auth_role_permission PRIMARY KEY CLUSTERED (role_code, permission_code),
                                          CONSTRAINT FK_auth_role_permission_role FOREIGN KEY (role_code)
                                              REFERENCES ShopFlow.auth_role (code),
                                          CONSTRAINT FK_auth_role_permission_permission FOREIGN KEY (permission_code)
                                              REFERENCES ShopFlow.auth_permission (code)
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_role_permission')
      AND name = N'IX_auth_role_permission_permission_code'
)
BEGIN
CREATE INDEX IX_auth_role_permission_permission_code
    ON ShopFlow.auth_role_permission (permission_code);
END;

IF OBJECT_ID(N'ShopFlow.auth_role_menu', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_role_menu (
                                    role_code NVARCHAR(50) NOT NULL,
                                    menu_code NVARCHAR(50) NOT NULL,
                                    CONSTRAINT PK_auth_role_menu PRIMARY KEY CLUSTERED (role_code, menu_code),
                                    CONSTRAINT FK_auth_role_menu_role FOREIGN KEY (role_code)
                                        REFERENCES ShopFlow.auth_role (code),
                                    CONSTRAINT FK_auth_role_menu_menu FOREIGN KEY (menu_code)
                                        REFERENCES ShopFlow.auth_menu (code)
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_role_menu')
      AND name = N'IX_auth_role_menu_menu_code'
)
BEGIN
CREATE INDEX IX_auth_role_menu_menu_code ON ShopFlow.auth_role_menu (menu_code);
END;

IF OBJECT_ID(N'ShopFlow.auth_refresh_token', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_refresh_token (
                                        id BIGINT IDENTITY(1, 1) NOT NULL,
                                        created_by NVARCHAR(100) NULL,
                                        created_at DATETIME2 NULL,
                                        updated_by NVARCHAR(100) NULL,
                                        updated_at DATETIME2 NULL,
                                        is_deleted INT NOT NULL CONSTRAINT DF_auth_refresh_token_is_deleted DEFAULT (0),
                                        user_code NVARCHAR(50) NOT NULL,
                                        token_hash NVARCHAR(500) NOT NULL,
                                        expires_at DATETIME2 NOT NULL,
                                        revoked_at DATETIME2 NULL,
                                        device_name NVARCHAR(200) NULL,
                                        ip_address NVARCHAR(45) NULL,
                                        CONSTRAINT PK_auth_refresh_token PRIMARY KEY CLUSTERED (id),
                                        CONSTRAINT UQ_auth_refresh_token_token_hash UNIQUE (token_hash),
                                        CONSTRAINT FK_auth_refresh_token_account FOREIGN KEY (user_code)
                                            REFERENCES ShopFlow.auth_user_account (code),
                                        CONSTRAINT CK_auth_refresh_token_is_deleted CHECK (is_deleted IN (0, 1))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_refresh_token')
      AND name = N'IX_auth_refresh_token_user_code'
)
BEGIN
CREATE INDEX IX_auth_refresh_token_user_code ON ShopFlow.auth_refresh_token (user_code);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_refresh_token')
      AND name = N'IX_auth_refresh_token_expires_at'
)
BEGIN
CREATE INDEX IX_auth_refresh_token_expires_at ON ShopFlow.auth_refresh_token (expires_at);
END;

IF OBJECT_ID(N'ShopFlow.auth_login_history', N'U') IS NULL
BEGIN
CREATE TABLE ShopFlow.auth_login_history (
                                        id BIGINT IDENTITY(1, 1) NOT NULL,
                                        created_by NVARCHAR(100) NULL,
                                        created_at DATETIME2 NULL,
                                        updated_by NVARCHAR(100) NULL,
                                        updated_at DATETIME2 NULL,
                                        is_deleted INT NOT NULL CONSTRAINT DF_auth_login_history_is_deleted DEFAULT (0),
                                        user_code NVARCHAR(50) NULL,
                                        username_entered NVARCHAR(100) NOT NULL,
                                        login_result NVARCHAR(20) NOT NULL,
                                        failure_reason NVARCHAR(255) NULL,
                                        ip_address NVARCHAR(45) NULL,
                                        user_agent NVARCHAR(500) NULL,
                                        CONSTRAINT PK_auth_login_history PRIMARY KEY CLUSTERED (id),
                                        CONSTRAINT FK_auth_login_history_account FOREIGN KEY (user_code)
                                            REFERENCES ShopFlow.auth_user_account (code),
                                        CONSTRAINT CK_auth_login_history_is_deleted CHECK (is_deleted IN (0, 1)),
                                        CONSTRAINT CK_auth_login_history_result CHECK (login_result IN (N'SUCCESS', N'FAILED'))
);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_login_history')
      AND name = N'IX_auth_login_history_user_created_at'
)
BEGIN
CREATE INDEX IX_auth_login_history_user_created_at
    ON ShopFlow.auth_login_history (user_code, created_at);
END;

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE object_id = OBJECT_ID(N'ShopFlow.auth_login_history')
      AND name = N'IX_auth_login_history_username_created_at'
)
BEGIN
CREATE INDEX IX_auth_login_history_username_created_at
    ON ShopFlow.auth_login_history (username_entered, created_at);
END;
