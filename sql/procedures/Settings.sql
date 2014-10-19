-- This file contains procedures for DefaultSettings functionality

DELIMITER // -- Tell MySQL how we will denote the end of each prepared statement

-- Gets a settings profile given its id
DROP PROCEDURE IF EXISTS getProfileById;
CREATE PROCEDURE getProfileById(IN _id INT)
	BEGIN
		SELECT * FROM default_settings WHERE id=_id;
	END //

DROP PROCEDURE IF EXISTS GetDefaultSettingsByIdAndType;
CREATE PROCEDURE GetDefaultSettingsByIdAndType(IN _prim_id INT, IN _type INT)
	BEGIN
		SELECT * FROM default_settings WHERE prim_id=_prim_id AND setting_type=_type;
	END //

-- Checks to see whether the given benchmark is a community default for any community
-- Author: Eric Burns
DROP PROCEDURE IF EXISTS IsBenchACommunityDefault;
CREATE PROCEDURE IsBenchACommunityDefault(IN _benchId INT)
	BEGIN
		SELECT count(*) as benchDefault
		FROM default_settings
		WHERE default_benchmark = _benchId AND setting_type=1;
	END //
	
-- Checks to see whether the given solver is a community default for any community
-- Author: Eric Burns
DROP PROCEDURE IF EXISTS IsSolverACommunityDefault;
CREATE PROCEDURE IsSolverACommunityDefault(IN _solverId INT)
	BEGIN
		SELECT count(*) as solverDefault
		FROM default_settings
		WHERE default_solver = _solverId AND setting_type=1;
	END //

-- Updates the maximum memory setting for a defaultsettings tuple
DROP PROCEDURE IF EXISTS SetMaximumMemorySetting;
CREATE PROCEDURE SetMaximumMemorySetting(IN _id INT, IN _bytes BIGINT)
	BEGIN
		UPDATE default_settings
		SET maximum_memory=_bytes
		WHERE id = _id;
	END //

-- Updates the default settings object with the given id
-- Author: Ruoyu Zhang
DROP PROCEDURE IF EXISTS SetDefaultSettingsById;
CREATE PROCEDURE SetDefaultSettingsById(IN _id INT, IN _num INT, IN _setting INT)
	BEGIN
      CASE _num
		WHEN 1 THEN
		UPDATE default_settings
		SET post_processor = _setting
		WHERE id = _id;
		
		WHEN 2 THEN
		UPDATE default_settings
		SET cpu_timeout = _setting
		WHERE id = _id;
		
		WHEN 3 THEN
		UPDATE default_settings
		SET clock_timeout = _setting
		WHERE id = _id;
		
		WHEN 4 THEN
		UPDATE default_settings
		SET dependencies_enabled=_setting
		WHERE id=_id;
		
		WHEN 5 THEN
		UPDATE default_settings
		SET default_benchmark=_setting
		WHERE id=_id;
		
		WHEN 6 THEN
		UPDATE default_settings
		SET pre_processor=_setting
		WHERE id=_id;
		
		WHEN 7 THEN
		UPDATE default_settings
		SET default_solver=_setting
		WHERE id=_id;
		
		WHEN 8 THEN
		UPDATE default_settings
		SET bench_processor=_setting
		WHERE id=_id;
		
    END CASE;
	END //
	
-- Insert a default setting of a space given by id when it's initiated.
-- Author: Ruoyu Zhang
DROP PROCEDURE IF EXISTS CreateDefaultSettings;
CREATE PROCEDURE CreateDefaultSettings(IN _prim_id INT, IN _pp INT, IN _cto INT, IN _clto INT, IN _dp BOOLEAN, IN _db INT, IN _dm BIGINT, IN _defaultSolver INT, IN _benchProc INT, IN _preProc INT, IN _type INT, IN _name VARCHAR(32), OUT _id INT)
	BEGIN
		INSERT INTO default_settings (prim_id, post_processor, cpu_timeout, clock_timeout, dependencies_enabled, default_benchmark, maximum_memory, default_solver, bench_processor, pre_processor, setting_type,name) VALUES (_prim_id, _pp, _cto, _clto, _dp, _db,_dm,default_solver,_benchProc, _preProc, _type,_name);
		SELECT LAST_INSERT_ID() INTO _id;

	END //

DELIMITER ; -- This should always be at the end of this file