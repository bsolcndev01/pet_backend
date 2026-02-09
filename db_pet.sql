-- 重置数据库（会删除已有同名库及其中数据）
DROP DATABASE IF EXISTS pet_health_system;
CREATE DATABASE IF NOT EXISTS pet_health_system;
USE pet_health_system;

-- 1. 用户角色表
CREATE TABLE user_roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. 用户表（已优化）
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    avatar_url VARCHAR(255),
    wechat_openid VARCHAR(100),
    notification_preferences JSON DEFAULT NULL,
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    FOREIGN KEY (role_id) REFERENCES user_roles(role_id)
);

-- 3. 宠物医疗机构表
CREATE TABLE medical_institutions (
    institution_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_name VARCHAR(100) NOT NULL,
    institution_type ENUM('宠物医院', '宠物诊所', '动物防疫站', '宠物美容医疗') NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    license_number VARCHAR(50),
    description TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at DATE NOT NULL
);

-- 4. 兽医专业信息表
CREATE TABLE veterinarians (
    vet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    institution_id INT NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    specialization VARCHAR(100),
    years_experience INT,
    qualification TEXT,
    position ENUM('主治医师', '助理医师', '院长', '专家') DEFAULT '主治医师',
    is_verified BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id)
);

-- 5. 宠物基本信息表（已优化）
CREATE TABLE pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pet_name VARCHAR(50) NOT NULL,
    species ENUM('狗', '猫', '兔', '仓鼠', '鸟', '其他') NOT NULL,
    breed VARCHAR(50),
    gender ENUM('公', '母', '未知') NOT NULL,
    birth_date DATE,
    color VARCHAR(50),
    temperament VARCHAR(50),
    current_weight DECIMAL(5,2),
    is_sterilized BOOLEAN DEFAULT FALSE,
    microchip_id VARCHAR(100),
    ear_id VARCHAR(100),
    body_type VARCHAR(50),
    special_needs TEXT,
    photo_url VARCHAR(255),
    health_status ENUM('优秀', '良好', '一般', '需要关注', '不佳') DEFAULT '良好',
    adopt_date DATE,
    registration_info VARCHAR(255),
    blood_type VARCHAR(20),
    allergies TEXT,
    chronic_diseases TEXT,
    genetic_risks TEXT,
    banned_drugs TEXT,
    last_medical_check DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_pets_user_id (user_id),
    INDEX idx_pets_species (species)
);

-- 5b. 宠物照片表（将图片二进制存到数据库）
CREATE TABLE pet_photos (
    photo_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    file_name VARCHAR(255),
    content_type VARCHAR(100),
    photo_data LONGBLOB NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    pos_x DOUBLE DEFAULT 0,
    pos_y DOUBLE DEFAULT 0,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    INDEX idx_pet_photos_pet_id (pet_id)
);

-- 6. 疫苗类型表
CREATE TABLE vaccine_types (
    vaccine_type_id INT AUTO_INCREMENT PRIMARY KEY,
    vaccine_name VARCHAR(100) NOT NULL,
    species ENUM('狗', '猫', '通用') NOT NULL,
    validity_period_months INT NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- 7. 疫苗接种记录表
CREATE TABLE vaccinations (
    vaccination_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    vaccine_type_id INT NOT NULL,
    institution_id INT NOT NULL,
    vet_id INT,
    vaccination_date DATE NOT NULL,
    next_due_date DATE NOT NULL,
    lot_number VARCHAR(50),
    notes TEXT,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (vaccine_type_id) REFERENCES vaccine_types(vaccine_type_id),
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id),
    FOREIGN KEY (vet_id) REFERENCES veterinarians(vet_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_vaccinations_pet_due (pet_id, next_due_date DESC)
);

-- 8. 医疗就诊记录表（已优化）
CREATE TABLE medical_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    institution_id INT NOT NULL,
    vet_user_id INT NOT NULL,
    visit_date DATE NOT NULL,
    reason TEXT NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescription TEXT,
    cost DECIMAL(8,2),
    payment_status ENUM('未支付', '部分支付', '已支付', '保险支付') DEFAULT '未支付',
    insurance_claim_id VARCHAR(50),
    follow_up_date DATE,
    record_status ENUM('初诊', '复诊', '急诊', '体检') DEFAULT '初诊',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id),
    FOREIGN KEY (vet_user_id) REFERENCES users(user_id),
    INDEX idx_medical_records_visit_date (visit_date),
    INDEX idx_medical_records_pet_date (pet_id, visit_date DESC),
    CONSTRAINT chk_follow_up_date CHECK (follow_up_date IS NULL OR follow_up_date >= visit_date)
);

-- 9. 体重记录表
CREATE TABLE weight_records (
    weight_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    record_date DATE NOT NULL,
    notes TEXT,
    recorded_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id)
);

-- 新增：体温记录表
CREATE TABLE temperature_records (
    temperature_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    temperature DECIMAL(4,1) NOT NULL,
    record_date DATE NOT NULL,
    notes TEXT,
    recorded_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id),
    INDEX idx_temperature_pet_date (pet_id, record_date DESC)
);

-- 新增：活动量记录表
CREATE TABLE activity_records (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    activity_level DECIMAL(6,2) NOT NULL,
    record_date DATE NOT NULL,
    notes TEXT,
    recorded_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id),
    INDEX idx_activity_pet_date (pet_id, record_date DESC)
);

-- 新增：饮水/进食记录表
CREATE TABLE intake_records (
    intake_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    intake_volume DECIMAL(8,2) NOT NULL,
    record_date DATE NOT NULL,
    notes TEXT,
    recorded_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id),
    INDEX idx_intake_pet_date (pet_id, record_date DESC)
);

-- 新增：用药与处方
CREATE TABLE medications (
    medication_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    vet_user_id INT NULL,
    drug_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(100),
    frequency VARCHAR(100),
    route VARCHAR(50),
    start_date DATE,
    end_date DATE,
    instructions TEXT,
    contraindications TEXT,
    status VARCHAR(50) DEFAULT '进行中',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (vet_user_id) REFERENCES users(user_id)
);

-- 10. 驱虫药品表
CREATE TABLE deworming_products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    deworming_type ENUM('内驱', '外驱', '内外同驱') NOT NULL,
    species ENUM('狗', '猫', '通用') NOT NULL,
    validity_period_days INT NOT NULL,
    dosage_guide TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- 11. 驱虫记录表
CREATE TABLE deworming_records (
    deworming_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    product_id INT NOT NULL,
    product_name VARCHAR(100) DEFAULT NULL,
    source_type ENUM('自驱', '医院') DEFAULT '自驱',
    institution_id INT NULL,
    vet_user_id INT NULL,
    application_date DATE NOT NULL,
    next_due_date DATE NOT NULL,
    dosage VARCHAR(50),
    notes TEXT,
    recorded_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES deworming_products(product_id),
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id),
    FOREIGN KEY (vet_user_id) REFERENCES users(user_id),
    FOREIGN KEY (recorded_by) REFERENCES users(user_id)
);

-- 12. 饮食记录表（不再依赖食物类型表）
CREATE TABLE diet_records (
    diet_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    food_type_id INT NOT NULL,
    feeding_time DATETIME NOT NULL,
    amount DECIMAL(6,2) NOT NULL,
    calorie_intake DECIMAL(6,2),
    feeding_type ENUM('早餐', '午餐', '晚餐', '加餐', '零食') NOT NULL,
    notes TEXT,
    recorded_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(user_id),
    INDEX idx_diet_records_pet_time (pet_id, feeding_time DESC)
);

-- 13. 提醒类型表
CREATE TABLE reminder_types (
    reminder_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL,
    source_table ENUM('vaccinations', 'deworming_records', 'medical_records', 'pets', 'diet_records') NOT NULL,
    source_field VARCHAR(50) NOT NULL,
    advance_days INT NOT NULL,
    template_message TEXT NOT NULL,
    is_auto_generated BOOLEAN DEFAULT TRUE
);

-- 15. 提醒与通知表
CREATE TABLE reminders (
    reminder_id INT AUTO_INCREMENT PRIMARY KEY,
    reminder_type_id INT NOT NULL,
    pet_id INT NOT NULL,
    user_id INT NOT NULL,
    vet_id INT NULL,
    source_table VARCHAR(50) NOT NULL,
    source_record_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    due_date DATE NOT NULL,
    reminder_date DATE NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    completed_date DATE NULL,
    sent_count INT DEFAULT 0,
    last_sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reminder_type_id) REFERENCES reminder_types(reminder_type_id),
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (vet_id) REFERENCES veterinarians(vet_id),
    INDEX idx_source_record (source_table, source_record_id)
);

-- 16b. 饮食/运动计划表
CREATE TABLE pet_plans (
    plan_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    plan_type ENUM('饮食', '运动') NOT NULL,
    title VARCHAR(100) NOT NULL,
    target VARCHAR(100),
    frequency VARCHAR(50),
    start_date DATE,
    end_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by INT,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- 16. 宠物共享表
CREATE TABLE pet_sharing (
    share_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    owner_user_id INT NOT NULL,
    shared_user_id INT NOT NULL,
    access_level ENUM('只读', '编辑') DEFAULT '只读',
    is_active BOOLEAN DEFAULT TRUE,
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at DATE NULL,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (owner_user_id) REFERENCES users(user_id),
    FOREIGN KEY (shared_user_id) REFERENCES users(user_id)
);

-- 17. 预约表（已优化）
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    
    owner_user_id INT NOT NULL,
    institution_id INT NOT NULL,
    vet_user_id INT,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    appointment_type ENUM('体检', '疫苗接种', '诊疗', '复诊', '急诊', '其他') NOT NULL,
    reason TEXT,
    status ENUM('待确认', '已确认', '已到店', '进行中', '已完成', '已取消', '已过期', '患者爽约', '医生改期') DEFAULT '待确认',
    auto_generated TINYINT(1) DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (owner_user_id) REFERENCES users(user_id),
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id),
    FOREIGN KEY (vet_user_id) REFERENCES users(user_id),
    INDEX idx_appointment_date (appointment_date, appointment_time),
    INDEX idx_status (status),
    INDEX idx_appointments_vet_date (vet_user_id, appointment_date, appointment_time)
);

-- 18. 系统日志表
CREATE TABLE system_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    action_type VARCHAR(50) NOT NULL,
    table_name VARCHAR(50),
    record_id INT,
    description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 19. 宠物健康指标表（新增）
CREATE TABLE pet_health_metrics (
    metric_id INT AUTO_INCREMENT PRIMARY KEY,
    species ENUM('狗', '猫', '兔', '仓鼠', '鸟', '其他') NOT NULL,
    breed VARCHAR(50),
    age_min_months INT,
    age_max_months INT,
    weight_min DECIMAL(5,2),
    weight_max DECIMAL(5,2),
    metric_type ENUM('体重', '体温', '心率', '呼吸频率') NOT NULL,
    normal_min DECIMAL(6,2),
    normal_max DECIMAL(6,2),
    unit VARCHAR(20),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 20. 药品库存管理表（新增）
CREATE TABLE medication_inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    institution_id INT NOT NULL,
    medication_name VARCHAR(100) NOT NULL,
    medication_type ENUM('处方药', '非处方药', '疫苗', '消毒用品') NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    unit VARCHAR(20),
    expiry_date DATE,
    purchase_date DATE,
    supplier VARCHAR(100),
    unit_price DECIMAL(8,2),
    storage_location VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id)
);

-- 21. 医疗影像记录表（新增）
CREATE TABLE medical_images (
    image_id INT AUTO_INCREMENT PRIMARY KEY,
    record_id INT NOT NULL,
    image_type ENUM('X光', 'B超', 'CT', '核磁共振', '照片') NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    description TEXT,
    taken_date DATE,
    taken_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (record_id) REFERENCES medical_records(record_id),
    FOREIGN KEY (taken_by) REFERENCES users(user_id)
);

-- 22. 兽医排班表（新增）
CREATE TABLE vet_schedules (
    schedule_id INT AUTO_INCREMENT PRIMARY KEY,
    vet_user_id INT NOT NULL,
    institution_id INT NOT NULL,
    schedule_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    max_appointments INT DEFAULT 10,
    status ENUM('正常', '休假', '培训', '会议') DEFAULT '正常',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_vet_schedule (vet_user_id, institution_id, schedule_date, start_time),
    FOREIGN KEY (vet_user_id) REFERENCES users(user_id),
    FOREIGN KEY (institution_id) REFERENCES medical_institutions(institution_id)
);

-- 23. 用户反馈表（新增）
CREATE TABLE user_feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    feedback_type ENUM('系统建议', '服务投诉', '功能需求', '问题报告') NOT NULL,
    content TEXT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    status ENUM('待处理', '处理中', '已解决', '已关闭') DEFAULT '待处理',
    response TEXT,
    responded_by INT,
    responded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (responded_by) REFERENCES users(user_id)
);

-- 24. 保险信息表（新增）
CREATE TABLE pet_insurance (
    insurance_id INT AUTO_INCREMENT PRIMARY KEY,
    pet_id INT NOT NULL,
    insurance_company VARCHAR(100) NOT NULL,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    coverage_type ENUM('基础医疗', '全面医疗', '意外险', '终身险') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    annual_premium DECIMAL(8,2),
    coverage_limit DECIMAL(10,2),
    deductible DECIMAL(8,2),
    contact_phone VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE CASCADE
);

-- 25. 费用与保险记录表（新增）
CREATE TABLE finance_records (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pet_id INT DEFAULT NULL,
    medical_record_id INT DEFAULT NULL,
    title VARCHAR(120) NOT NULL,
    record_type ENUM('消费', '报销') DEFAULT '消费',
    category VARCHAR(80),
    amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    record_date DATE NOT NULL,
    status ENUM('未报销', '处理中', '已报销', '自费') DEFAULT '未报销',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(pet_id) ON DELETE SET NULL,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(record_id) ON DELETE SET NULL,
    INDEX idx_finance_user_date (user_id, record_date DESC)
);

-- ============================================
-- 创建视图
-- ============================================

-- 宠物健康摘要视图
CREATE VIEW v_pet_health_summary AS
SELECT 
    p.pet_id,
    p.pet_name,
    p.species,
    p.breed,
    p.health_status,
    p.current_weight,
    p.last_medical_check,
    (SELECT COUNT(*) FROM medical_records mr WHERE mr.pet_id = p.pet_id) as total_visits,
    (SELECT MAX(visit_date) FROM medical_records mr WHERE mr.pet_id = p.pet_id) as last_visit,
    (SELECT COUNT(*) FROM vaccinations v WHERE v.pet_id = p.pet_id) as vaccine_count,
    (SELECT COUNT(*) FROM deworming_records dr WHERE dr.pet_id = p.pet_id) as deworming_count
FROM pets p;

-- ============================================
-- 创建存储过程
-- ============================================

DELIMITER //

-- 存储过程：获取宠物即将到期的项目
CREATE PROCEDURE sp_get_pet_upcoming_due_items(
    IN p_pet_id INT,
    IN p_days_ahead INT
)
BEGIN
    -- 即将到期的疫苗接种
    SELECT 
        '疫苗接种' as item_type,
        v.vaccination_id as item_id,
        vt.vaccine_name as item_name,
        v.next_due_date as due_date,
        DATEDIFF(v.next_due_date, CURDATE()) as days_remaining
    FROM vaccinations v
    JOIN vaccine_types vt ON v.vaccine_type_id = vt.vaccine_type_id
    WHERE v.pet_id = p_pet_id 
        AND v.next_due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL p_days_ahead DAY)
    
    UNION ALL
    
    -- 即将到期的驱虫
    SELECT 
        '驱虫提醒' as item_type,
        dr.deworming_id as item_id,
        dp.product_name as item_name,
        dr.next_due_date as due_date,
        DATEDIFF(dr.next_due_date, CURDATE()) as days_remaining
    FROM deworming_records dr
    JOIN deworming_products dp ON dr.product_id = dp.product_id
    WHERE dr.pet_id = p_pet_id 
        AND dr.next_due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL p_days_ahead DAY)
    
    UNION ALL
    
    -- 即将到来的复诊
    SELECT 
        '复诊提醒' as item_type,
        mr.record_id as item_id,
        CONCAT('复诊:', LEFT(mr.diagnosis, 20)) as item_name,
        mr.follow_up_date as due_date,
        DATEDIFF(mr.follow_up_date, CURDATE()) as days_remaining
    FROM medical_records mr
    WHERE mr.pet_id = p_pet_id 
        AND mr.follow_up_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL p_days_ahead DAY)
        AND mr.follow_up_date IS NOT NULL
        
    ORDER BY due_date ASC;
END //

-- 存储过程：每日备份（示例）
CREATE PROCEDURE sp_daily_backup_main_tables()
BEGIN
    -- 这里可以使用MySQL的备份命令或导出到文件
    -- 实际部署中应考虑使用mysqldump或物理备份
    INSERT INTO system_logs (action_type, description) 
    VALUES ('BACKUP', 'Daily backup initiated');
END //

DELIMITER ;

-- ============================================
-- 创建触发器
-- ============================================

DELIMITER //

-- 触发器：禁止兽医账号成为宠物主人（新增）
CREATE TRIGGER trg_block_vet_pet_owner_insert
BEFORE INSERT ON pets
FOR EACH ROW
BEGIN
    DECLARE role_name_var VARCHAR(50);
    SELECT ur.role_name INTO role_name_var
    FROM users u
    JOIN user_roles ur ON u.role_id = ur.role_id
    WHERE u.user_id = NEW.user_id
    LIMIT 1;
    IF role_name_var = '兽医' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '兽医账号不能创建宠物档案';
    END IF;
END //

-- 触发器：禁止兽医账号成为宠物主人（更新）
CREATE TRIGGER trg_block_vet_pet_owner_update
BEFORE UPDATE ON pets
FOR EACH ROW
BEGIN
    DECLARE role_name_var VARCHAR(50);
    SELECT ur.role_name INTO role_name_var
    FROM users u
    JOIN user_roles ur ON u.role_id = ur.role_id
    WHERE u.user_id = NEW.user_id
    LIMIT 1;
    IF role_name_var = '兽医' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '兽医账号不能创建宠物档案';
    END IF;
END //

-- 触发器：更新宠物最新体重
CREATE TRIGGER trg_update_pet_weight
AFTER INSERT ON weight_records
FOR EACH ROW
BEGIN
    UPDATE pets 
    SET current_weight = NEW.weight, 
        updated_at = CURRENT_TIMESTAMP
    WHERE pet_id = NEW.pet_id;
END //

-- 触发器：自动创建疫苗接种提醒
CREATE TRIGGER trg_auto_vaccination_reminder
AFTER INSERT ON vaccinations
FOR EACH ROW
BEGIN
    DECLARE reminder_days INT DEFAULT 0;
    DECLARE pet_name_var VARCHAR(50);
    DECLARE vaccine_name_var VARCHAR(100);
    DECLARE rt_id INT DEFAULT NULL;
    
    -- 获取提醒配置
    SELECT reminder_type_id, advance_days INTO rt_id, reminder_days 
    FROM reminder_types 
    WHERE type_name = '疫苗接种' AND is_auto_generated = TRUE
    LIMIT 1;
    
    IF rt_id IS NOT NULL THEN
        -- 获取宠物和疫苗信息
        SELECT p.pet_name, vt.vaccine_name INTO pet_name_var, vaccine_name_var
        FROM pets p
        JOIN vaccine_types vt ON vt.vaccine_type_id = NEW.vaccine_type_id
        WHERE p.pet_id = NEW.pet_id;
        
        -- 创建提醒
        INSERT INTO reminders (
            reminder_type_id,
            pet_id,
            user_id,
            source_table,
            source_record_id,
            title,
            message,
            due_date,
            reminder_date
        ) VALUES (
            rt_id,
            NEW.pet_id,
            NEW.created_by,
            'vaccinations',
            NEW.vaccination_id,
            CONCAT(vaccine_name_var, '到期提醒'),
            CONCAT(pet_name_var, '的', vaccine_name_var, '将于', NEW.next_due_date, '到期，请及时安排接种'),
            NEW.next_due_date,
            DATE_SUB(NEW.next_due_date, INTERVAL reminder_days DAY)
        );
    END IF;
END //

-- 触发器：审计用户敏感信息修改
CREATE TRIGGER trg_audit_user_changes
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    IF OLD.email != NEW.email OR OLD.phone != NEW.phone THEN
        INSERT INTO system_logs (user_id, action_type, table_name, record_id, description)
        VALUES (
            NEW.user_id, 
            'UPDATE_SENSITIVE', 
            'users', 
            NEW.user_id,
            CONCAT('用户修改了敏感信息：', 
                   IF(OLD.email != NEW.email, CONCAT('邮箱从', OLD.email, '改为', NEW.email), ''),
                   IF(OLD.phone != NEW.phone, CONCAT('电话从', OLD.phone, '改为', NEW.phone), '')
            )
        );
    END IF;
END //

DELIMITER //

-- ============================================
-- 创建事件
-- ============================================

-- 创建每日备份事件
CREATE EVENT IF NOT EXISTS evt_daily_backup
ON SCHEDULE EVERY 1 DAY
STARTS '2024-03-16 02:00:00'
DO
BEGIN
    CALL sp_daily_backup_main_tables();
END //

DELIMITER ;

-- ============================================
-- 数据清理（可用于已存在数据的环境）
-- ============================================

-- 清理兽医账号名下的宠物（会触发级联删除相关记录）
DELETE p FROM pets p
JOIN users u ON p.user_id = u.user_id
JOIN user_roles r ON u.role_id = r.role_id
WHERE r.role_name = '兽医';

-- ============================================
-- 插入基础数据
-- ============================================

-- 插入用户角色
INSERT INTO user_roles (role_name, description) VALUES
('宠物主人', '可以管理自己的宠物健康档案'),
('兽医', '可以查看和编辑经手的宠物医疗记录'),
('系统管理员', '拥有系统全部管理权限');

-- 插入宠物医疗机构
INSERT INTO medical_institutions (institution_name, institution_type, address, phone, email, license_number, description, is_verified, created_at) VALUES
('爱心宠物医院', '宠物医院', '北京市朝阳区建国路100号', '010-12345678', 'lovepet@hospital.com', 'HOS2024001', '综合性宠物医院，24小时急诊', TRUE, '2010-05-01'),
('宠物之家诊所', '宠物诊所', '上海市浦东新区陆家嘴金融中心', '021-87654321', 'pethome@clinic.com', 'CLI2024001', '专业宠物诊疗和美容服务', TRUE, '2015-09-12'),
('动物医疗中心', '宠物医院', '广州市天河区体育西路', '020-23456789', 'animalcare@hospital.com', 'HOS2024002', '大型动物医疗中心，设备先进', TRUE, '2008-03-20'),
('宝贝宠物诊所', '宠物诊所', '深圳市南山区科技园', '0755-34567890', 'babypet@clinic.com', 'CLI2024002', '专注于小型动物诊疗', TRUE, '2018-06-15'),
('城市动物防疫站', '动物防疫站', '杭州市西湖区文三路', '0571-45678901', 'citypet@station.com', 'STA2024001', '政府指定的动物防疫机构', TRUE, '2005-01-01');

-- 插入用户数据
INSERT INTO users (username, email, password_hash, phone, address, role_id) VALUES
-- 宠物主人
('alice', '2939899744@qq.com', '123456', '13800138001', '北京市朝阳区', 1),
('lisi', 'lisi@email.com', 'hashed_password_2', '13900139002', '上海市浦东新区', 1),
('wangwu', 'wangwu@email.com', 'hashed_password_3', '13700137003', '广州市天河区', 1),
('zhaoliu', 'zhaoliu@email.com', 'hashed_password_4', '13600136004', '深圳市南山区', 1),
('sunqi', 'sunqi@email.com', 'hashed_password_5', '13500135005', '杭州市西湖区', 1),
-- 兽医
('dr_zhang', 'drzhang@hospital.com', 'hashed_password_6', '13800138006', '北京市朝阳区', 2),
('dr_li', 'drli@clinic.com', '123456', '13900139007', '上海市浦东新区', 2),
('dr_wang', 'drwang@hospital.com', 'hashed_password_8', '13700137008', '广州市天河区', 2),
('dr_zhao', 'drzhao@clinic.com', 'hashed_password_9', '13600136009', '深圳市南山区', 2),
('dr_sun', 'drsun@station.com', 'hashed_password_10', '13500135010', '杭州市西湖区', 2),
-- 系统管理员
('admin', 'admin1@system.com', '123456', '13600136011', '系统管理部', 3),
('admin2', 'admin2@system.com', 'hashed_password_12', '13500135012', '系统管理部', 3);

-- 插入兽医专业信息
INSERT INTO veterinarians (user_id, institution_id, license_number, specialization, years_experience, qualification, position, is_verified) VALUES
(6, 1, 'VET2024001', '内科', 8, '兽医学博士', '主治医师', TRUE),
(7, 2, 'VET2024002', '外科', 5, '兽医学硕士', '助理医师', TRUE),
(8, 3, 'VET2024003', '皮肤病专科', 10, '兽医学博士', '专家', TRUE),
(9, 4, 'VET2024004', '牙科', 6, '兽医学硕士', '主治医师', TRUE),
(10, 5, 'VET2024005', '防疫', 12, '兽医学博士', '院长', TRUE);

-- 插入宠物数据（含扩展字段，未填的置为 NULL）
INSERT INTO pets (
    user_id, pet_name, species, breed, gender, birth_date, color, temperament, current_weight,
    is_sterilized, microchip_id, special_needs, photo_url, health_status,
    body_type, ear_id, adopt_date, registration_info, blood_type, allergies,
    chronic_diseases, genetic_risks, banned_drugs, last_medical_check
) VALUES
(1, '旺财', '狗', '金毛寻回犬', '公', '2022-03-15', '金色', '温顺友善', 28.50, TRUE, 'CHIP00123456', NULL, NULL, '优秀', '大型', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(1, '咪咪', '猫', '英国短毛猫', '母', '2023-01-20', '蓝色', '活泼好奇', 4.20, TRUE, 'CHIP00123457', NULL, NULL, '良好', '小型', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(2, '小白', '狗', '比熊犬', '公', '2021-08-10', '白色', '粘人可爱', 6.80, TRUE, 'CHIP00123458', NULL, NULL, '良好', '小型', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(3, '小黑', '猫', '中华田园猫', '公', '2022-11-05', '黑色', '独立高冷', 5.10, FALSE, 'CHIP00123459', NULL, NULL, '一般', '中型', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(4, '兔八哥', '兔', '垂耳兔', '公', '2023-03-01', '灰色', '胆小安静', 2.30, TRUE, NULL, NULL, NULL, '优秀', '小型', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(11, '可乐', '猫', '橘猫', '公', '2023-06-12', '橘白相间', '亲人爱撒娇', 5.40, TRUE, 'CHIP00123460', '易焦虑，出行需提前安抚', NULL, '良好', '中型', NULL, '2023-08-01', '社区登记编号 HX-2023-0821', 'A', '海鲜轻微过敏', '轻度肥胖风险', '暂无', '氯霉素禁用', '2024-12-20');

-- 插入疫苗类型
INSERT INTO vaccine_types (vaccine_name, species, validity_period_months, description) VALUES
('狂犬疫苗', '通用', 12, '预防狂犬病，法律要求每年接种'),
('六联疫苗', '狗', 12, '预防犬瘟热、细小病毒等六种疾病'),
('猫三联', '猫', 12, '预防猫瘟、猫鼻支等三种疾病'),
('八联疫苗', '狗', 12, '预防八种犬类常见传染病'),
('狂犬疫苗(三年)', '通用', 36, '三年有效期的狂犬疫苗');

-- 提前插入提醒类型，供触发器使用
INSERT INTO reminder_types (type_name, source_table, source_field, advance_days, template_message, is_auto_generated) VALUES
('疫苗接种', 'vaccinations', 'next_due_date', 30, '{pet_name}的{vaccine_name}将于{due_date}到期，请及时安排接种', TRUE),
('驱虫提醒', 'deworming_records', 'next_due_date', 7, '{pet_name}的{product_name}驱虫将于{due_date}到期', TRUE);

-- 插入疫苗接种记录
INSERT INTO vaccinations (pet_id, vaccine_type_id, institution_id, vet_id, vaccination_date, next_due_date, lot_number, notes, created_by) VALUES
(1, 1, 1, 1, '2024-01-15', '2025-01-15', 'LOT20240115', '接种后无不良反应', 6),
(1, 2, 1, 1, '2024-01-15', '2025-01-15', 'LOT20240116', '年度加强免疫', 6),
(2, 3, 2, 2, '2024-02-20', '2025-02-20', 'LOT20240220', '首次接种', 7),
(3, 1, 3, 3, '2024-01-10', '2025-01-10', 'LOT20240110', NULL, 8),
(4, 3, 4, 4, '2024-03-05', '2025-03-05', 'LOT20240305', '加强免疫', 9);

-- 插入医疗就诊记录
INSERT INTO medical_records (pet_id, institution_id, vet_user_id, visit_date, reason, diagnosis, treatment, prescription, cost, follow_up_date, record_status) VALUES
(1, 1, 6, '2024-03-01', '呕吐、食欲不振', '急性肠胃炎', '输液治疗、禁食24小时', '肠胃宝，每日两次，连用5天', 350.00, '2024-03-08', '急诊'),
(2, 2, 7, '2024-02-15', '年度体检', '健康状况良好', '常规体检、驱虫', '无', 200.00, '2025-02-15', '体检'),
(3, 3, 8, '2024-03-10', '皮肤瘙痒、掉毛', '真菌感染', '药浴治疗、外用喷剂', '抗真菌喷剂，每日两次', 280.00, '2024-03-24', '初诊'),
(1, 1, 6, '2024-01-20', '牙结石清洁', '轻度牙结石', '超声波洁牙', '口腔消炎药，每日一次', 450.00, NULL, '初诊'),
(4, 4, 9, '2024-02-28', '打喷嚏、流鼻涕', '上呼吸道感染', '抗生素治疗', '阿莫西林，每日一次', 180.00, '2024-03-07', '急诊'),
(6, 2, 7, '2025-02-18', '呕吐伴软便', '轻度肠胃炎', '补液+益生菌+饮食调整', '蒙脱石散，每日2次；益生菌，每日1次', 180.00, '2025-02-25', '复诊'),
(2, 2, 7, '2025-02-20', '年度体检', '总体健康，建议控制体重', '常规体检+体脂评估', '无处方，建议低脂粮', 220.00, '2026-02-20', '体检');

-- 插入预约记录
INSERT INTO appointments (pet_id, owner_user_id, institution_id, vet_user_id, appointment_date, appointment_time, appointment_type, reason, status, auto_generated, notes) VALUES
(1, 1, 1, 2, '2025-01-05', '10:00:00', '体检', '年度体检', '已确认', 0, '请提前10分钟到达'),
(2, 3, 1, 3, '2025-01-06', '14:00:00', '疫苗接种', '年度疫苗', '待确认', 0, NULL),
(3, 1, 2, 2, '2025-01-07', '09:30:00', '诊疗', '皮肤复诊', '已确认', 0, '带上既往病历'),
(1, 1, 2, 6, '2025-01-10', '15:00:00', '复诊', '肠胃复查', '待确认', 0, '需空腹，带上化验单'),
(2, 1, 1, 6, '2025-01-12', '11:30:00', '疫苗接种', '狂犬疫苗加强', '待确认', 0, '携带免疫证'),
(3, 1, 2, 2, '2025-01-15', '16:00:00', '体检', '皮肤复诊+体检', '待确认', 0, '提前半小时到店'),
(6, 11, 2, 7, '2025-02-18', '10:30:00', '诊疗', '季节性呕吐复查', '已确认', 0, '可乐需空腹8小时'),
(2, 1, 2, 7, '2025-02-20', '15:00:00', '体检', '年度体检+体脂评估', '待确认', 0, 'owner：alice，如需改期请提前联系'),
(4, 3, 2, 7, '2025-02-22', '11:00:00', '复诊', '皮肤状况回访', '已确认', 0, '带上上次的喷剂'),
(6, 11, 2, 7, '2025-02-21', '09:10:00', '诊疗', '春季肠胃不适复诊', '已到店', 0, '今日待诊队列，等待叫号'),
(2, 1, 2, 7, '2025-02-21', '10:00:00', '复诊', '体脂复查+营养调整', '进行中', 0, '现场接诊：诊室2，复核饮食记录'),
(1, 1, 2, 7, '2025-02-21', '11:00:00', '诊疗', '胃部用药复查', '待确认', 0, '今日加号，待主人确认到诊'),
(3, 1, 2, 7, '2025-02-22', '16:30:00', '复诊', '皮肤复查+药浴方案确认', '待确认', 0, '需提前 24 小时避免洗澡'),
(2, 1, 2, 7, '2025-02-23', '09:00:00', '体检', '年度复评+血检', '已确认', 0, '抽血前禁食 8 小时'),
(1, 1, 1, 7, '2025-03-02', '09:30:00', '复诊', '术后复查', '待确认', 0, '近期预约：空腹'),
(2, 1, 2, 7, '2025-03-03', '10:00:00', '体检', '年度复查', '已确认', 0, '携带疫苗本'),
(6, 11, 2, 7, '2025-03-04', '15:30:00', '诊疗', '肠胃随访', '待确认', 0, '注意饮食记录');

-- 插入费用/保险记录
INSERT INTO finance_records (user_id, pet_id, title, record_type, category, amount, record_date, status, notes) VALUES
(1, 1, '年度体检费用', '消费', '体检', 350.00, '2025-01-05', '已报销', '含血检'),
(1, 1, '宠物保险年费', '消费', '保险理赔', 800.00, '2025-01-02', '自费', '平安宠物险续费'),
(3, 2, '疫苗报销', '报销', '药品', 180.00, '2025-01-06', '处理中', '等待保险理赔');

-- 插入饮食/运动计划
INSERT INTO pet_plans (pet_id, plan_type, title, target, frequency, start_date, end_date, notes, created_by) VALUES
(1, '饮食', '控制体重-轻食方案', '每日热量 900 kcal', '每日三餐', '2025-01-01', '2025-02-15', '高蛋白低脂，减少零食，每周复称', 1),
(1, '运动', '关节友好运动', '每日步行 40 分钟', '每周5天', '2025-01-01', '2025-03-01', '避免剧烈跳跃，分上午/傍晚两次', 1),
(2, '饮食', '增肌营养计划', '每日热量 650 kcal', '每日四餐少量', '2025-01-05', '2025-02-28', '增加优质蛋白，补充牛磺酸', 3);

-- 插入用药/处方
INSERT INTO medications (pet_id, vet_user_id, drug_name, dosage, frequency, route, start_date, end_date, instructions, contraindications, status) VALUES
(1, 6, '复方阿莫西林', '250mg', '每天2次', '口服', '2025-01-02', '2025-01-08', '饭后服用，足量饮水', '青霉素过敏禁用', '已完成');

-- 插入健康提醒
INSERT INTO reminder_types (type_name, source_table, source_field, advance_days, template_message, is_auto_generated) VALUES
('健康随访', 'medical_records', 'follow_up_date', 3, '请按时随访复诊', FALSE)
ON DUPLICATE KEY UPDATE type_name=VALUES(type_name);

INSERT INTO reminder_types (type_name, source_table, source_field, advance_days, template_message, is_auto_generated) VALUES
('用药随访', 'medical_records', 'follow_up_date', 2, '{pet_name}用药随访，请确认服药依从性与不良反应', FALSE)
ON DUPLICATE KEY UPDATE type_name=VALUES(type_name);

INSERT INTO reminders (reminder_type_id, pet_id, user_id, vet_id, source_table, source_record_id, title, message, due_date, reminder_date, is_completed, sent_count)
VALUES
((SELECT reminder_type_id FROM reminder_types WHERE type_name='健康随访' LIMIT 1), 1, 1, 1, 'medical_records', 1, '胃部复诊提醒', '请携带胃药复诊，空腹到院', '2025-01-10', '2025-01-08', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='健康随访' LIMIT 1), 2, 1, 2, 'medical_records', 2, '年度体检提醒', '年度体检，请带疫苗本', '2025-01-12', '2025-01-09', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='健康随访' LIMIT 1), 6, 11, 2, 'medical_records', 6, '复诊随访：胃肠复查', '联系主人确认症状变化，如有持续呕吐提前安排复诊', '2025-02-25', '2025-02-23', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='用药随访' LIMIT 1), 6, 11, 2, 'medical_records', 6, '用药提醒：蒙脱石散+益生菌', '请回访主人确认按时服药，留意腹泻缓解与不良反应', '2025-02-20', '2025-02-19', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='用药随访' LIMIT 1), 2, 1, 2, 'medical_records', 7, '用药提醒：体重管理建议', '复核饮食控制与日常补剂使用，评估是否需要调整方案', '2025-03-01', '2025-02-27', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='疫苗接种' LIMIT 1), 1, 1, 1, 'vaccinations', 1, '近期疫苗提醒', '三天后狂犬疫苗复查，带疫苗本', '2025-03-02', '2025-02-28', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='驱虫提醒' LIMIT 1), 2, 1, 2, 'deworming_records', 2, '驱虫到期提醒', '补齐外驱滴剂，检查皮肤情况', '2025-03-03', '2025-02-28', FALSE, 0),
((SELECT reminder_type_id FROM reminder_types WHERE type_name='健康随访' LIMIT 1), 6, 11, 2, 'medical_records', 6, '胃肠复诊回访', '确认饮食与症状，必要时提前复诊', '2025-03-04', '2025-03-02', FALSE, 0);

-- 插入体重记录
INSERT INTO weight_records (pet_id, weight, record_date, notes, recorded_by) VALUES
(1, 27.80, '2024-01-01', '新年首次称重', 1),
(1, 28.20, '2024-02-01', '体重稳定', 1),
(1, 28.50, '2024-03-01', '理想体重', 1),
(2, 4.00, '2024-01-15', '正常范围', 1),
(2, 4.20, '2024-03-01', '轻微增长', 1);

-- 插入体温记录（宠物ID=1，近7天示例）
INSERT INTO temperature_records (pet_id, temperature, record_date, notes, recorded_by) VALUES
(1, 38.5, '2024-12-01', '精神良好', 1),
(1, 38.6, '2024-12-02', '正常', 1),
(1, 38.4, '2024-12-03', '正常', 1),
(1, 38.7, '2024-12-04', '运动后略高', 1),
(1, 38.5, '2024-12-05', '正常', 1),
(1, 38.6, '2024-12-06', '正常', 1),
(1, 38.5, '2024-12-07', '正常', 1);

-- 插入活动量记录（宠物ID=1，近7天示例，单位为自定义活动指数）
INSERT INTO activity_records (pet_id, activity_level, record_date, notes, recorded_by) VALUES
(1, 65.0, '2024-12-01', '日常散步', 1),
(1, 70.0, '2024-12-02', '增加户外时间', 1),
(1, 62.0, '2024-12-03', '阴雨天活动少', 1),
(1, 68.0, '2024-12-04', '正常活动', 1),
(1, 71.0, '2024-12-05', '玩耍较多', 1),
(1, 66.0, '2024-12-06', '正常活动', 1),
(1, 69.0, '2024-12-07', '公园散步', 1);

-- 插入饮水/进食记录（宠物ID=1，近7天示例，单位ml）
INSERT INTO intake_records (pet_id, intake_volume, record_date, notes, recorded_by) VALUES
(1, 420.0, '2024-12-01', '饮水正常', 1),
(1, 450.0, '2024-12-02', '进食良好', 1),
(1, 400.0, '2024-12-03', '略少', 1),
(1, 460.0, '2024-12-04', '正常', 1),
(1, 480.0, '2024-12-05', '运动后补水', 1),
(1, 430.0, '2024-12-06', '正常', 1),
(1, 440.0, '2024-12-07', '正常', 1);

-- 插入驱虫药品
INSERT INTO deworming_products (product_name, deworming_type, species, validity_period_days, dosage_guide) VALUES
('拜宠清', '内驱', '狗', 90, '按体重每10kg服用1片'),
('福来恩', '外驱', '通用', 30, '按体重选择相应规格'),
('大宠爱', '内外同驱', '通用', 30, '按体重选择相应规格，猫狗通用'),
('犬心保', '内驱', '狗', 30, '预防心丝虫，每月一次'),
('博来恩', '内外同驱', '猫', 30, '专为猫咪设计的内外同驱');

-- 插入驱虫记录
INSERT INTO deworming_records (pet_id, product_id, application_date, next_due_date, dosage, notes, recorded_by) VALUES
(1, 1, '2024-03-01', '2024-06-01', '1片', '按体重10kg计算', 1),
(1, 2, '2024-03-01', '2024-04-01', '1支', '滴剂', 1),
(2, 3, '2024-02-15', '2024-03-15', '0.5ml', '体重4kg', 1),
(3, 4, '2024-03-05', '2024-04-05', '1片', NULL, 2),
(4, 5, '2024-02-28', '2024-03-28', '0.8ml', '猫用', 3);

-- 插入饮食记录
INSERT INTO diet_records (pet_id, food_type_id, feeding_time, amount, calorie_intake, feeding_type, notes, recorded_by) VALUES
(1, 1, '2024-03-15 08:00:00', 300.00, 1050.00, '早餐', '食欲良好，全部吃完', 1),
(1, 1, '2024-03-15 18:00:00', 300.00, 1050.00, '晚餐', '正常进食', 1),
(2, 2, '2024-03-15 07:30:00', 60.00, 228.00, '早餐', '自由采食', 1),
(3, 4, '2024-03-15 08:15:00', 100.00, 360.00, '早餐', '正常进食', 2),
(4, 3, '2024-03-15 18:30:00', 85.00, 72.25, '晚餐', '很喜欢这个口味', 3);

-- 插入药品库存
INSERT INTO medication_inventory (institution_id, medication_name, medication_type, quantity, unit, expiry_date, purchase_date, supplier, unit_price, storage_location) VALUES
(1, '阿莫西林胶囊', '处方药', 120, '盒', '2025-12-31', '2024-06-01', '华东医药', 25.80, '库房A1'),
(2, '狂犬病疫苗', '疫苗', 60, '支', '2025-08-15', '2024-05-20', '国药控股', 58.00, '冷链02');

-- 插入医疗影像记录
INSERT INTO medical_images (record_id, image_type, image_url, description, taken_date, taken_by) VALUES
(1, 'X光', '/images/records/1-xray.jpg', '右后肢检查', '2024-05-10', 6),
(2, 'B超', '/images/records/2-ultrasound.jpg', '腹部B超复查', '2024-05-12', 7);

-- 插入兽医排班
INSERT INTO vet_schedules (vet_user_id, institution_id, schedule_date, start_time, end_time, max_appointments, status) VALUES
(6, 1, '2024-06-10', '09:00:00', '17:00:00', 12, '正常'),
(7, 2, '2024-06-10', '13:00:00', '18:00:00', 10, '正常');

-- 插入用户反馈
INSERT INTO user_feedback (user_id, feedback_type, content, rating, status, response, responded_by, responded_at) VALUES
(1, '系统建议', '希望增加成长曲线导出功能', 5, '处理中', '已列入需求排期', 11, '2024-05-30 15:20:00'),
(2, '问题报告', '预约提醒未收到推送', 3, '已解决', '已修复推送配置，请重试', 11, '2024-05-29 10:15:00');

-- 插入保险信息
INSERT INTO pet_insurance (pet_id, insurance_company, policy_number, coverage_type, start_date, end_date, annual_premium, coverage_limit, deductible, contact_phone, notes) VALUES
(1, '平安宠物险', 'PA-202406-0001', '全面医疗', '2024-01-01', '2024-12-31', 1299.00, 50000.00, 300.00, '400-000-0000', '含口腔保障'),
(2, '众安保险', 'ZA-202406-0002', '意外险', '2024-02-01', '2025-01-31', 899.00, 30000.00, 200.00, '400-888-9999', '含救援服务');
