DELIMITER //

-- Retrieve the `id` of a given Event
DROP PROCEDURE IF EXISTS GetEventId;
CREATE PROCEDURE GetEventId( IN _name CHAR(32) )
	BEGIN
		SELECT event_id FROM analytics_events WHERE name=_name;
	END //

-- If there is not yet a record of this event happening today
--   create a record and set its count to `1`
-- otherwise
--   increment the count of the existing record
DROP PROCEDURE IF EXISTS RecordEvent;
CREATE PROCEDURE RecordEvent(
		IN _event_id INT,
		IN _date_recorded DATE)
	BEGIN
		IF NOT EXISTS (SELECT count FROM analytics_historical WHERE event_id=_event_id AND date_recorded=_date_recorded) THEN
			INSERT INTO analytics_historical (event_id, date_recorded, count)
				VALUES (_event_id, _date_recorded, 1);
		ELSE
			UPDATE analytics_historical SET count = count + 1 WHERE event_id=_event_id AND date_recorded=_date_recorded;
		END IF;
	END //

DELIMITER ;