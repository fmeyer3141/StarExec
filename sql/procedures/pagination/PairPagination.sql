-- This file contains a highly repetitive procedure used to sort datatables on different columns
-- our old method was to use a order by (CASE) statement to order on different columns, but doing this
-- prevents SQL from using indexes for sorting for some reason, and as such is very slow. 

DELIMITER // -- Tell MySQL how we will denote the end of each prepared statement

-- Gets the fewest necessary JobPairs in order to service a client's
-- request for the next page of JobPairs in their DataTable object.  
-- This services the DataTable object by supporting filtering by a query, 
-- ordering results by a column, and sorting results in ASC or DESC order.  
-- Author: Todd Elvers + Eric Burns
DROP PROCEDURE IF EXISTS GetNextPageOfJobPairsInJobSpace;
CREATE PROCEDURE GetNextPageOfJobPairsInJobSpace(IN _startingRecord INT, IN _recordsPerPage INT, IN _sortASC BOOLEAN, IN _query TEXT, IN _spaceId INT, IN _sortColumn INT, IN _stageNumber INT)
	BEGIN
		IF (_sortColumn = 0) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						bench_id,
						bench_name,
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)
				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				-- Order results depending on what column is being sorted on
				ORDER BY bench_name ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						bench_id,
						bench_name,
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id

				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')

				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY bench_name DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		ELSEIF (_sortColumn=1) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						bench_id,
						bench_name,
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Order results depending on what column is being sorted on
				ORDER BY jobpair_stage_data.solver_name ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId  AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY jobpair_stage_data.solver_name DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		ELSEIF (_sortColumn=2) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Order results depending on what column is being sorted on
				ORDER BY jobpair_stage_data.config_name ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY jobpair_stage_data.config_name DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		ELSEIF (_sortColumn=3) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Order results depending on what column is being sorted on
				ORDER BY jobpair_stage_data.status_code ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY jobpair_stage_data.status_code DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		ELSEIF (_sortColumn=4) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR     job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Order results depending on what column is being sorted on
				ORDER BY wallclock ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY wallclock DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		ELSEIF (_sortColumn=5) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))

				-- Order results depending on what column is being sorted on
				ORDER BY result ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 				
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value			LIKE 	CONCAT('%', _query, '%'))

				ORDER BY result DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
			ELSEIF (_sortColumn=6) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						bench_id,
						bench_name,
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 	
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Order results depending on what column is being sorted on
				ORDER BY job_pairs.id ASC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				 
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				ORDER BY job_pairs.id DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
			ELSEIF (_sortColumn=7) THEN
			IF (_sortASC = TRUE) THEN
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						bench_id,
						bench_name,
						job_attributes.attr_value AS result,
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
						
				FROM	job_pairs	
				LEFT JOIN job_pair_completion ON job_pair_completion.pair_id=job_pairs.id
				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				-- Exclude JobPairs whose benchmark name, configuration name, solver name, status and wallclock
				-- don't include the query
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value			LIKE 	CONCAT('%', _query, '%'))
				
				-- Doing a negative DESC is the same as ASC but with null values last
				ORDER BY -completion_id DESC
			 
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT 	job_pairs.id, 
						jobpair_stage_data.config_id,
						jobpair_stage_data.config_name,
						
						jobpair_stage_data.status_code,
						jobpair_stage_data.solver_id,
						jobpair_stage_data.solver_name,
						
						bench_id,
						bench_name,
						
						job_attributes.attr_value AS result,
						
						jobpair_stage_data.wallclock AS wallclock,
						jobpair_stage_data.cpu AS cpu
				FROM	job_pairs	
				
				LEFT JOIN job_pair_completion ON job_pair_completion.pair_id=job_pairs.id

				LEFT JOIN job_attributes on (job_attributes.pair_id=job_pairs.id and job_attributes.attr_key="starexec-result")
				JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
				WHERE 	jobpair_stage_data.job_space_id=_spaceId AND 
				((_stageNumber = 0 AND jobpair_stage_data.stage_number=job_pairs.primary_jobpair_data) OR jobpair_stage_data.stage_number=_stageNumber)

				
				AND		(bench_name 		LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.config_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.solver_name		LIKE	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.status_code 	LIKE 	CONCAT('%', _query, '%')
				OR		jobpair_stage_data.wallclock				LIKE	CONCAT('%', _query, '%')
				OR		cpu				LIKE	CONCAT('%', _query, '%')
				OR      job_attributes.attr_value 			LIKE 	CONCAT('%', _query, '%'))
				
				-- Doing a negative ASC is the same as DESC but with null values last

				ORDER BY -completion_id ASC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
		END IF;
	END //
	

-- Gets the next page of job pairs running on a given node for a datatable
	
DROP PROCEDURE IF EXISTS GetNextPageOfRunningJobPairs;
CREATE PROCEDURE GetNextPageOfRunningJobPairs(IN _startingRecord INT, IN _recordsPerPage INT, IN _sortASC BOOLEAN, IN _id INT)
	BEGIN
		
			IF (_sortASC = TRUE) THEN
				SELECT *
				FROM job_pairs
				WHERE node_id = _id AND (job_pairs.status_code = 4 OR job_pairs.status_code = 3)
				ORDER BY sge_id ASC
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
				SELECT *
				FROM job_pairs
				WHERE node_id = _id AND (job_pairs.status_code = 4 OR job_pairs.status_code = 3)
				ORDER BY sge_id DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
	END //
	

-- Gets the next page of job pairs enqueued in a given queue for a datatable
DROP PROCEDURE IF EXISTS GetNextPageOfEnqueuedJobPairs;
CREATE PROCEDURE GetNextPageOfEnqueuedJobPairs(IN _startingRecord INT, IN _recordsPerPage INT, IN _sortASC BOOLEAN, IN _id INT)
	BEGIN
		
			IF (_sortASC = TRUE) THEN
					SELECT *
					FROM job_pairs
					-- Where the job_pair is running on the input Queue
						INNER JOIN jobs AS enqueued ON job_pairs.job_id = enqueued.id
						JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id
					WHERE (enqueued.queue_id = _id AND job_pairs.status_code = 2)
					ORDER BY job_pairs.sge_id ASC
				-- Shrink the results to only those required for the next page of JobPairs
				LIMIT _startingRecord, _recordsPerPage;
			ELSE
					SELECT *
					FROM job_pairs
					-- Where the job_pair is running on the input Queue
						INNER JOIN jobs AS enqueued ON job_pairs.job_id = enqueued.id
						JOIN jobpair_stage_data ON jobpair_stage_data.jobpair_id = job_pairs.id

					WHERE (enqueued.queue_id = _id AND job_pairs.status_code = 2)
					ORDER BY job_pairs.sge_id DESC
				LIMIT _startingRecord, _recordsPerPage;
			END IF;
	END //
DELIMITER ; -- this should always be at the end of the file