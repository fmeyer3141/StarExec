package org.starexec.test.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.starexec.data.database.Jobs;
import org.starexec.data.database.Processors;
import org.starexec.data.database.Queues;
import org.starexec.data.database.Solvers;
import org.starexec.data.database.Spaces;
import org.starexec.data.database.Uploads;
import org.starexec.data.database.Users;
import org.starexec.data.to.Configuration;
import org.starexec.data.to.Job;
import org.starexec.data.to.Permission;
import org.starexec.data.to.Processor;
import org.starexec.data.to.Processor.ProcessorType;
import org.starexec.data.to.Queue;
import org.starexec.data.to.Solver;
import org.starexec.data.to.Space;
import org.starexec.data.to.User;
import org.starexec.jobs.JobManager;
import org.starexec.servlets.BenchmarkUploader;
import org.starexec.servlets.ProcessorManager;
import org.starexec.test.TestUtil;
import org.starexec.util.ArchiveUtil;
import org.starexec.util.Util;


/**
 * This file contains functions for loading test objects into the database.
 * Test objects are created with random names to avoid getting repeat
 * names when running tests multiple times.
 * @author Eric Burns
 *
 */

public class ResourceLoader {
	private static final Logger log = Logger.getLogger(ResourceLoader.class);	
	
	
	/**
	 * Returns the path to the resource directory, which is where we are storing files like solvers
	 * and benchmarks that are used during processing
	 * @return The absolute file path
	 */
	public static String getResourcePath() {
		return ResourceLoader.class.getResource("/org/starexec/test/resources").getFile();
	}
	
	/**
	 * Returns a File object representing the file with the given name from the resource directory
	 * @param name The name of the file, which must be present in the resource directory
	 * @return 
	 */
	public static File getResource(String name) {
		return new File(ResourceLoader.class.getResource("/org/starexec/test/resources/"+name).getFile());
	}
	
	/**
	 * Returns a File object representing the directory where all downloads performed during testing
	 * should be placed. For example, StarexecCommand tests use this downloads directory.
	 * @return
	 */
	public static File getDownloadDirectory() {
		String filePath=getResourcePath();
		File file=new File(filePath,"downloads");
		file.mkdir();
		return file;
	}
	
	/**
	 * Loads a processor into the database
	 * @param fileName The name of the file in the resource directory
	 * @param type Either post, bench, or pre processor
	 * @param communityId The ID of the community to place the processor in
	 * @return The Processor object, with all of its attributes set (name, ID, etc.)
	 */
	public static Processor loadProcessorIntoDatabase(String fileName,ProcessorType type, int communityId) {
		try {
			File file=new File(fileName);
			Processor p=new Processor();
			p.setName(TestUtil.getRandomSolverName());
			p.setCommunityId(communityId);
			p.setType(type);
			
			File newFile = ProcessorManager.getProcessorFilePath(communityId, p.getName());
			File processorFile=getResource(fileName);
			FileUtils.copyFile(processorFile, newFile);
			
			if (!newFile.setExecutable(true, false)) {			
				log.warn("Could not set processor as executable: " + newFile.getAbsolutePath());
			}
			p.setFilePath(newFile.getAbsolutePath());			

			int id=Processors.add(p);
			if (id>0) {
				p.setId(id);
				return p;
			}
			
		} catch (Exception e) {
			log.error("loadProcessorIntoDatabase says "+e.getMessage(),e);
		}
		return null;
		
	}
	
	/**
	 * This will load a job with the given solvers and benchmarks into the database, after which it should
	 * be picked up by the periodic task and started running. It is soemwhat primitive right now-- for every solver
	 * a single configuration will be picked randomly to be added to the job, and the job will be added to a random
	 * queue that the given user owns
	 * @param spaceId The ID of the space to put the job in
	 * @param userId The ID of the user who will own the job
	 * @param solvers The solverIds that will be matched to every benchmark
	 * @param benchmarks The benchmarkIDs to run
	 * @return The job object
	 */
	public static Job loadJobIntoDatabase(int spaceId, int userId, int preProcessorId, int postProcessorId, List<Integer> solverIds, List<Integer> benchmarkIds) {
		
		Space space=Spaces.get(spaceId);
		String name=TestUtil.getRandomJobName();
		Queue q=Queues.getUserQueues(userId).get(0);
		Job job=JobManager.setupJob(userId, name, "test job", preProcessorId, postProcessorId, q.getId());
		
		
		List<Integer> configIds=new ArrayList<Integer>();
		for (Integer i : solverIds) {
			configIds.add(Solvers.getConfigsForSolver(i).get(0).getId());
		}
		List<Space> spaces=new ArrayList<Space>();
		spaces.add(Spaces.get(spaceId));
		HashMap<Integer,String> SP = new HashMap<Integer,String>();
		SP.put(spaceId, Spaces.get(spaceId).getName());
		JobManager.buildJob(job, userId, 100, 100, Util.gigabytesToBytes(1), benchmarkIds, solverIds, configIds, spaceId, SP);
		
		Jobs.add(job, spaceId);
		return job;
	}
	
	/**
	 * Loads a configuration for a solver into the database
	 * @param fileName The name of the file in the resource directory
	 * @param solverId The ID of the solver to give the configuration to
	 * @return The Configuration object with all of its fields set (name, ID, etc.)
	 */
	
	public static Configuration loadConfigurationFileIntoDatabase(String fileName, int solverId)  {
		try {
			File file=getResource(fileName);
			return loadConfigurationIntoDatabase(FileUtils.readFileToString(file), solverId);

		} catch(Exception e) {
			log.error("loadConfigurationIntoDatabase says "+e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * Loads a configuration for a solver into the database
	 * @param contents The actual String configuration to give to the solver
	 * @param solverId The ID of the solver to give the configuration to
	 * @return The Configuration object with all of its fields set (name, ID, etc.)
	 */
	
	public static Configuration loadConfigurationIntoDatabase(String contents,int solverId)  {
		try {
			Configuration c=new Configuration();
			c.setName(TestUtil.getRandomSolverName());
			c.setSolverId(solverId);
			Solver solver=Solvers.get(solverId);
			// Build a path to the appropriate solver bin directory and ensure the file pointed to by newConfigFile doesn't already exist
			File newConfigFile = new File(Util.getSolverConfigPath(solver.getPath(), c.getName()));
			// If a configuration file exists on disk with the same name, just throw an error. This really should never
			//happen because we are given the configs long, random names, so we have a problem if this occurs
			if(newConfigFile.exists()){
				return null;
			}
			FileUtils.writeStringToFile(newConfigFile, contents);
			
			// Make sure the configuration has the right line endings
			Util.normalizeFile(newConfigFile);
			
			//Makes executable
			newConfigFile.setExecutable(true);
			int id=Solvers.addConfiguration(solver, c);
			if (id>0) {
				c.setId(id);
				return c;
			}
	 		
			return null;
		} catch (Exception e){
			log.error("loadConfigurationIntoDatabase says "+e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * Loads an archive of benchmarks into the database
	 * @param archiveName The name of the archive containing the benchmarks in the Resource directory
	 * @param parentSpaceId The ID of the space to place the benchmarks in. Benchmarks will
	 * not be made into a hierarchy-- they will all be placed into the given space
	 * @param userId The ID of the owner of the benchmarks
	 * @return
	 */
	public static List<Integer> loadBenchmarksIntoDatabase(String archiveName, int parentSpaceId, int userId) {
		try {
			File archive=getResource(archiveName);
			
			//make a copy of the archive, because the benchmark extraction function will delete the archive
			File archiveCopy=new File(getDownloadDirectory(),UUID.randomUUID()+archive.getName());
			FileUtils.copyFile(archive, archiveCopy);
			Integer statusId = Uploads.createUploadStatus(parentSpaceId, userId);
			Permission p=new Permission();
			List<Integer> ids=BenchmarkUploader.handleUploadRequestAfterExtraction(archiveCopy, userId, parentSpaceId, 1, false, p, 
					"dump", statusId, false, false, null);
			return ids;
		} catch (Exception e) {
			log.error("loadBenchmarksIntoDatabase says "+e.getMessage(),e);
		}
		return null;

	}
	/**
	 * Loads a solver into the database
	 * @param archiveName The name of the archive containing the solver in the Resource directory
	 * @param parentSpaceId The ID of the parent space for the solver
	 * @param userId The ID of the user that will own the solver
	 * @return The Solver object will all of its fields set.
	 */
	public static Solver loadSolverIntoDatabase(String archiveName, int parentSpaceId, int userId) {
		try {
			Solver s=new Solver();
			s.setName(TestUtil.getRandomSolverName());
			s.setDescription("solver coming from here");
			s.setUserId(userId);
			File archive=getResource(archiveName);
			String filePath=Solvers.getDefaultSolverPath(userId, s.getName());
			s.setPath(filePath);
			File solverDir=new File(filePath);
			solverDir.mkdirs();
			
			FileUtils.copyFileToDirectory(archive, solverDir);
			File archiveFile=new File(solverDir,s.getName());
			ArchiveUtil.extractArchive(archiveFile.getAbsolutePath());
			
			//Find configurations from the top-level "bin" directory
			for(Configuration c : Solvers.findConfigs(solverDir.getAbsolutePath())) {
				s.addConfiguration(c);
			}
			
			int id=Solvers.add(s, parentSpaceId);
			if (id>0) {
				s.setId(id);
				return s;
			} else {
				// there was an error of some kind
				return null;
			}
		} catch (Exception e) {
			log.error("loadSolverIntoDatabase says "+e.getMessage(),e);
		}
		return null;	
	}
	
	/**
	 * Loads a new space into the database
	 * @param userId The ID of the user who is creating the space
	 * @param parentSpaceId The ID of the parent space for the new subspace
	 * @return The Space object with all of its fields set.
	 */
	public static Space loadSpaceIntoDatabase(int userId, int parentSpaceId) {
		Space space=new Space();
		space.setName(TestUtil.getRandomSpaceName());
		space.setDescription("test desc");
		int id=Spaces.add(space, parentSpaceId, userId);
		if (id>0) {
			space.setId(id);
			return space;
		} else {
			return null;
		}
	}
	/**
	 * Loads a user into the database, without any particular name, email, password, and so on. Useful for testing.
	 * @return The user, with their ID and all parameters set, or null on error
	 */
	public static User loadUserIntoDatabase() {
		return loadUserIntoDatabase("test","user",TestUtil.getRandomPassword(),TestUtil.getRandomPassword(),"The University of Iowa","test");
	}
	/**
	 * Creates a user with the given attributes and adds them to the database
	 * @param fname The first name for the user
	 * @param lname The last name for the user
	 * @param email The email of the user
	 * @param password The plaintext password for the user
	 * @param institution
	 * @param role The role of the user-- should be either "user" or "admin"
	 * @return The User on success, or null on error. Their ID will be set on success.
	 */
	public static User loadUserIntoDatabase(String fname, String lname, String email, String password, String institution, String role) {
		User u=new User();
		u.setFirstName(fname);
		u.setLastName(lname);
		u.setPassword(password);
		u.setEmail(email);
		u.setInstitution(institution);
		u.setRole(role);
		int id=Users.add(u);
		if (id>0) {
			u.setId(id);
			return u;
		}
		log.debug("loadUserIntoDatabase could not generate a user, returning null");
		return null;
	}
	
}
