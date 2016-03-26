package org.starexec.test.integration.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.mockito.Mockito;
import org.starexec.app.RESTServices;
import org.starexec.data.database.AnonymousLinks;
import org.starexec.data.database.AnonymousLinks.PrimitivesToAnonymize;
import org.starexec.data.database.Benchmarks;
import org.starexec.data.database.Communities;
import org.starexec.data.database.Jobs;
import org.starexec.data.database.Queues;
import org.starexec.data.database.Solvers;
import org.starexec.data.database.Spaces;
import org.starexec.data.database.Uploads;
import org.starexec.data.database.Users;
import org.starexec.data.database.Websites;
import org.starexec.data.security.ValidatorStatusCode;
import org.starexec.data.to.*;
import org.starexec.data.to.Processor.ProcessorType;
import org.starexec.data.to.Status.StatusCode;
import org.starexec.data.to.Website.WebsiteType;
import org.starexec.test.TestUtil;
import org.starexec.test.integration.StarexecTest;
import org.starexec.test.integration.TestSequence;
import org.starexec.test.resources.ResourceLoader;

import com.google.gson.Gson;

/**
 * This class contains tests for RESTServices that should get rejected for security reasons. These
 * tests exist to make sure that requests that would violate permissions actually do get rejected. Tests
 * for successful RESTService calls should be written in another class.
 * @author Eric Burns
 *
 */
public class RESTServicesSecurityTests extends TestSequence {
	private RESTServices services = new RESTServices();
	User user = null; // user that should have no permissions to do anything
	User admin = null;
	
	// all of these will be owned by the admin
	Space space = null;
	Solver solver = null;
	List<Integer> benchmarkIds = null;
	Job job = null;
	BenchmarkUploadStatus benchmarkStatus = null;
	SpaceXMLUploadStatus spaceStatus = null;
	Website solverWebsite = null;
	int invalidBenchId = 0;
	String anonymousJobId = null;
	private Gson gson = new Gson();
	Processor postProcessor = null;
	private void assertResultIsInvalid(String result) {
		Assert.assertFalse(gson.fromJson(result, ValidatorStatusCode.class).isSuccess());
	}
	
	@StarexecTest
	private void restartStarexecTest() throws Exception {
		String result = services.restartStarExec(TestUtil.getMockHttpRequest(user.getId()));
		assertResultIsInvalid(result);
	}
	
	@StarexecTest
	private void getFinishedPairsForMatrixTest() {
		assertResultIsInvalid(services.getFinishedJobPairsForMatrix(job.getPrimarySpace(), 
				1, TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getFinishedJobPairsForMatrix(-1, 1, TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void getSolverComparisonsPaginatedTest() {
		int cId = job.getJobPairs().get(0).getPrimaryConfiguration().getId();

		assertResultIsInvalid(services.getSolverComparisonsPaginated(false, job.getPrimarySpace(), cId, cId,
				TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getSolverComparisonsPaginated(false, -1, cId, cId, TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void getAnonymousLinkForPrimitiveTest() {
		assertResultIsInvalid(services.getAnonymousLinkForPrimitive("job",job.getId(),"none", TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getAnonymousLinkForPrimitive("job",-1,"none", TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getAnonymousLinkForPrimitive("invalid",job.getId(),"none", TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getJobPairsPaginatedWithAnonymousLinkTest() {
		assertResultIsInvalid(services.getJobPairsPaginatedWithAnonymousLink(anonymousJobId,1, false,
				-1,false,"none", TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getJobPairsPaginatedWithAnonymousLink("",1, false,
				job.getPrimarySpace(),false,"none", TestUtil.getMockHttpRequest(admin.getId())));
	}
	

	@StarexecTest
	private void getAnonymousSpaceOverviewGraphTest() {
		assertResultIsInvalid(services.getSpaceOverviewGraph(1,anonymousJobId,
				-1,"none", TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getSpaceOverviewGraph(1,"",
				job.getPrimarySpace(),"none", TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getAnonymousSolverComparisonGraphTest() {
		int cId = job.getJobPairs().get(0).getPrimaryConfiguration().getId();
		assertResultIsInvalid(services.getAnonymousSolverComparisonGraph(anonymousJobId,1,
				-1,cId,cId,100,"black","none", TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getAnonymousSolverComparisonGraph("",1,
				job.getPrimarySpace(),cId,cId,100,"none","black",TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getSpaceOverviewGraphTest() {
		assertResultIsInvalid(services.getSpaceOverviewGraph(1,-1, TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getSpaceOverviewGraph(1,job.getPrimarySpace(),TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void getAnonymousJobStatsTest() {
		assertResultIsInvalid(services.getAnonymousJobStatsPaginated(1,-1,
				anonymousJobId,"none",false,false, TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getAnonymousJobStatsPaginated(1,job.getPrimarySpace(),
				"","none",false,false, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getJobStatsTest() {
		assertResultIsInvalid(services.getJobStatsPaginated(1,-1,false,false, TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getJobStatsPaginated(1,job.getPrimarySpace(),false,false, TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void getSolverComparisonGraphTest() {
		int cId = job.getJobPairs().get(0).getPrimaryConfiguration().getId();
		assertResultIsInvalid(services.getSolverComparisonGraph(1,
				-1,cId,cId,100,"black", TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getSolverComparisonGraph(1,
				job.getPrimarySpace(),cId,cId,100,"black",TestUtil.getMockHttpRequest(user.getId())));
	}
	
	
	@StarexecTest
	private void getJobPairsPaginatedTest() {
		assertResultIsInvalid(services.getJobPairsPaginated(1, false,
				job.getPrimarySpace(),false,TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getJobPairsPaginated(1, false,
				-1,false, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	
	@StarexecTest
	private void getJobPairsInSpaceHierarchyByConfigPaginatedTest() {
		int cId = job.getJobPairs().get(0).getPrimaryConfiguration().getId();
		assertResultIsInvalid(services.
				getJobPairsInSpaceHierarchyByConfigPaginated(1, false, job.getPrimarySpace(),"all",cId, TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.
				getJobPairsInSpaceHierarchyByConfigPaginated(1, false, -1,"all",cId, TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.
				getJobPairsInSpaceHierarchyByConfigPaginated(1, false, job.getPrimarySpace(),"badtypestring",cId, TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void recompileJobSpacesTest() {
		assertResultIsInvalid(services.recompileJobSpaces(job.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.recompileJobSpaces(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void postProcessJobTest() {
		assertResultIsInvalid(services.postProcessJob(job.getId(),1,postProcessor.getId(),TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.postProcessJob(job.getId(),1,-1,TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.postProcessJob(-1,1,postProcessor.getId(),TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void rerunAllPairsTest() {
		assertResultIsInvalid(services.rerunAllJobPairs(job.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.rerunAllJobPairs(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void rerunTimelessPairsTest() {
		assertResultIsInvalid(services.rerunTimelessJobPairs(job.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.rerunTimelessJobPairs(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void rerunSinglePairTest() {
		assertResultIsInvalid(services.rerunJobPair(job.getJobPairs().get(0).getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.rerunJobPair(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void rerunAllPairsOfStatusTest() {
		assertResultIsInvalid(services.rerunJobPairs(job.getId(), StatusCode.STATUS_KILLED.getVal(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.rerunJobPairs(-1, StatusCode.STATUS_KILLED.getVal(), TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void editJobNameTest() {
		assertResultIsInvalid(services.editJobName(job.getId(), "test name", TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.editJobName(-1, "test name", TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void editJobDescTest() {
		assertResultIsInvalid(services.editJobDescription(job.getId(), "test desc", TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.editJobDescription(-1, "test desc", TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getAllBenchInSpaceTest() {
		assertResultIsInvalid(services.getAllBenchmarksInSpace(space.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getAllBenchmarksInSpace(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getPrimitiveDetailsPaginatedTest() {
		assertResultIsInvalid(services.getPrimitiveDetailsPaginated(space.getId(),"solver", TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getPrimitiveDetailsPaginated(-1,"solver", TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getSolverWebsitesTest() {
		assertResultIsInvalid(services.getWebsites("solver",solver.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getWebsites("solver",-1, TestUtil.getMockHttpRequest(admin.getId())));
		assertResultIsInvalid(services.getWebsites("invalid",solver.getId(), TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getSpaceWebsitesTest() {
		assertResultIsInvalid(services.getWebsites("space",space.getId(), TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void deleteWebsiteTest() {
		assertResultIsInvalid(services.deleteWebsite(solverWebsite.getId(), TestUtil.getMockHttpRequest(user.getId())));
	}
	
	private Map<String,String> getNameAndUrlParams() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", "testname");
		params.put("url", "http://www.testsite.edu");
		return params;
	}
	
	@StarexecTest
	private void addSolverWebsiteTest() {
		assertResultIsInvalid(services.addWebsite("solver",solver.getId(), TestUtil.getMockHttpRequest(user.getId(), getNameAndUrlParams())));
		assertResultIsInvalid(services.addWebsite("solver",-1, TestUtil.getMockHttpRequest(admin.getId(),getNameAndUrlParams())));
		assertResultIsInvalid(services.addWebsite("invalid",solver.getId(), TestUtil.getMockHttpRequest(admin.getId(),getNameAndUrlParams())));
	}
	
	@StarexecTest
	private void addSpaceWebsiteTest() {
		assertResultIsInvalid(services.addWebsite("space",space.getId(), TestUtil.getMockHttpRequest(user.getId(), getNameAndUrlParams())));
		assertResultIsInvalid(services.addWebsite("space",-1, TestUtil.getMockHttpRequest(admin.getId(),getNameAndUrlParams())));
	}
	
	@StarexecTest
	private void addUserWebsiteTest() {
		assertResultIsInvalid(services.addWebsite("user",admin.getId(), TestUtil.getMockHttpRequest(user.getId(), getNameAndUrlParams())));
		assertResultIsInvalid(services.addWebsite("user",-1, TestUtil.getMockHttpRequest(admin.getId(),getNameAndUrlParams())));
	}
	
	@StarexecTest
	private void addUsersToSpaceTest() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("copyToSubspaces", "true");
		assertResultIsInvalid(services.addUsersToSpace(space.getId(),TestUtil.getMockHttpRequest(user.getId(), params, getSelectedIdParams(user.getId()))));
		assertResultIsInvalid(services.addUsersToSpace(-1,TestUtil.getMockHttpRequest(admin.getId(), params, getSelectedIdParams(user.getId()))));
		assertResultIsInvalid(services.addUsersToSpace(space.getId(),TestUtil.getMockHttpRequest(admin.getId(), new HashMap<String,String>(), getSelectedIdParams(user.getId()))));
		assertResultIsInvalid(services.addUsersToSpace(space.getId(),TestUtil.getMockHttpRequest(admin.getId(),params)));
	}
	
	@StarexecTest
	private void copySolversToSpaceTest() {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("copyToSubspaces", "true");
		params.put("copy", "true");
		assertResultIsInvalid(services.copySolversToSpace(space.getId(),
				TestUtil.getMockHttpRequest(user.getId(), params, getSelectedIdParams(solver.getId())), response));
		assertResultIsInvalid(services.copySolversToSpace(-1,
				TestUtil.getMockHttpRequest(admin.getId(), params, getSelectedIdParams(solver.getId())), response));
		assertResultIsInvalid(services.copySolversToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), params), response));
		assertResultIsInvalid(services.copySolversToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), params, getSelectedIdParams(-1)), response));
		assertResultIsInvalid(services.copySolversToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), new HashMap<String,String>(), getSelectedIdParams(solver.getId())), response));
	}
	
	@StarexecTest
	private void copyBenchmarksToSpaceTest() {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("copy", "true");
		assertResultIsInvalid(services.copyBenchToSpace(space.getId(),
				TestUtil.getMockHttpRequest(user.getId(), params, getSelectedIdParams(benchmarkIds.get(0))), response));
		assertResultIsInvalid(services.copyBenchToSpace(-1,
				TestUtil.getMockHttpRequest(admin.getId(), params, getSelectedIdParams(benchmarkIds.get(0))), response));
		assertResultIsInvalid(services.copyBenchToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), params), response));
		assertResultIsInvalid(services.copyBenchToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), params, getSelectedIdParams(-1)), response));
		assertResultIsInvalid(services.copyBenchToSpace(space.getId(),
				TestUtil.getMockHttpRequest(admin.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0))), response));
	}
	
	@StarexecTest
	private void getBenchmarkUploadDescription() {
		assertResultIsInvalid(services.getBenchmarkUploadDescription(benchmarkStatus.getId(), TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.getBenchmarkUploadDescription(-1, TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void getInvalidUploadedBenchmarkOutput() {
		Assert.assertTrue(services.getInvalidUploadedBenchmarkOutput(invalidBenchId, 
				TestUtil.getMockHttpRequest(user.getId())).contains("You may only view your own benchmark uploads"));
	}
	
	@StarexecTest
	private void getJobPairLogTest() {
		Assert.assertTrue(services.getJobPairLog(job.getJobPairs().get(0).getId(), 
				TestUtil.getMockHttpRequest(user.getId())).contains("does not have access"));
		Assert.assertTrue(services.getJobPairLog(-1, 
				TestUtil.getMockHttpRequest(admin.getId())).contains("does not have access"));
	}
	
	@StarexecTest
	private void getJobPairStdoutTest() {
		Assert.assertTrue(services.getJobPairStdout(job.getJobPairs().get(0).getId(), 1, 100,
				TestUtil.getMockHttpRequest(user.getId())).contains("not available"));
		Assert.assertTrue(services.getJobPairStdout(-1, 1, 100,
				TestUtil.getMockHttpRequest(admin.getId())).contains("not available"));
	}
	
	@StarexecTest
	private void getBenchmarkContentTest() {
		Assert.assertTrue(services.getBenchmarkContent(benchmarkIds.get(0), 100, 
				TestUtil.getMockHttpRequest(user.getId())).contains("not available"));
		Assert.assertTrue(services.getBenchmarkContent(-1, 100,
				TestUtil.getMockHttpRequest(admin.getId())).contains("not available"));
	}
	
	@StarexecTest
	private void getSolverBuildLogTest() {
		Assert.assertTrue(services.getSolverBuildLog(solver.getId(),
				TestUtil.getMockHttpRequest(user.getId())).contains("not available"));
		Assert.assertTrue(services.getSolverBuildLog(-1,
				TestUtil.getMockHttpRequest(admin.getId())).contains("not available"));
	}
	
	@StarexecTest
	private void editUserInfoTest() {
		assertResultIsInvalid(services.editUserInfo("firstname",admin.getId(),"newname",TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.editUserInfo("badattr",user.getId(),"newname",TestUtil.getMockHttpRequest(user.getId())));
		assertResultIsInvalid(services.editUserInfo("firstname",user.getId(),TestUtil.getRandomAlphaString(500),TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void setSettingsProfileForUserTest() {
		assertResultIsInvalid(services.setSettingsProfileForUser(Communities.getDefaultSettings(space.getId()).getId(),user.getId(),TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void deleteDefaultSettingsTest() {
		assertResultIsInvalid(services.deleteDefaultSettings(Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void editCommunityDefaultSettingsTest() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("val", solver.getId()+"");
		assertResultIsInvalid(services.editCommunityDefaultSettings("defaultsolver",Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(user.getId(), params)));
		assertResultIsInvalid(services.editCommunityDefaultSettings("fakeattr",Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(admin.getId(), params)));
	}
	
	@StarexecTest
	private void editCommunityTest() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("val", solver.getId()+"");
		assertResultIsInvalid(services.editCommunityDetails("name",Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(user.getId(), params)));
		assertResultIsInvalid(services.editCommunityDetails("fakeattr",Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(admin.getId(), params)));
	}
	
	@StarexecTest
	private void editSpaceTest() {
		Map<String,String> params = new HashMap<String,String>();
		params.put("name", "name");
		params.put("description","desc");
		params.put("locked","false");
		assertResultIsInvalid(services.editSpace(Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(user.getId(), params)));
		assertResultIsInvalid(services.editSpace(Communities.getDefaultSettings(space.getId()).getId(),TestUtil.getMockHttpRequest(admin.getId(), new HashMap<String,String>())));
	}
	
	@StarexecTest
	private void editQueueInfoTest() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("cpuTimeout", "10");
		params.put("wallTimeout", "10");
		assertResultIsInvalid(services.editQueueInfo(Queues.getAllQ().getId(),TestUtil.getMockHttpRequest(user.getId(), params)));
	}
	
	@StarexecTest
	private void editQueueInfoInvalidParamsTest() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("cpuTimeout", "test");
		params.put("wallTimeout", "10");
		assertResultIsInvalid(services.editQueueInfo(Queues.getAllQ().getId(),TestUtil.getMockHttpRequest(admin.getId(), params)));
	}
	
	@StarexecTest
	private void reinstateUserTest() {
		String result = services.reinstateUser(admin.getId(),TestUtil.getMockHttpRequest(user.getId()));
		assertResultIsInvalid(result);
	}
	
	@StarexecTest
	private void cleanErrorStatesTest() {
		assertResultIsInvalid(services.clearErrorStates(TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void getAllPrimitiveDetailsPaginationTest() {
		assertResultIsInvalid(services.getAllPrimitiveDetailsPagination("user",TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void runTestsByNameTest() {
		assertResultIsInvalid(services.runTest(TestUtil.getMockHttpRequest(user.getId())));
	}
	
	@StarexecTest
	private void runAllTestsTest() {
		assertResultIsInvalid(services.runAllTests(TestUtil.getMockHttpRequest(user.getId())));
	}
	
	private static HashMap<String,List<String>> getSelectedIdParams(Object...objects) {
		HashMap<String,List<String>> map = new HashMap<String,List<String>>();
		List<String> strs = new ArrayList<String>();
		for (Object o : objects) {
			strs.add(o.toString());
		}
		map.put("selectedIds[]", strs);
		return map;
	}
	
	@StarexecTest
	private void editProcessorTest() {
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("name", "newname");
		params.put("desc", "newdesc");
		assertResultIsInvalid(services.editProcessor(postProcessor.getId(),TestUtil.getMockHttpRequest(
				user.getId(), params)));
		assertResultIsInvalid(services.editProcessor(postProcessor.getId(),TestUtil.getMockHttpRequest(
				admin.getId())));
	}
	
	@StarexecTest
	private void leaveCommunityTest() {
		assertResultIsInvalid(services.leaveCommunity(space.getId(),TestUtil.getMockHttpRequest(
				user.getId())));
		assertResultIsInvalid(services.leaveCommunity(-1,TestUtil.getMockHttpRequest(admin.getId())));
	}
	
	@StarexecTest
	private void deleteProcessorsTest() {
		assertResultIsInvalid(services.deleteProcessors(TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(postProcessor.getId()))));
		assertResultIsInvalid(services.deleteProcessors(TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
	}
	
	
	@StarexecTest
	private void removeBenchmarksTest() {
		assertResultIsInvalid(services.removeBenchmarksFromSpace(space.getId(),TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
		assertResultIsInvalid(services.removeBenchmarksFromSpace(space.getId(),TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
		assertResultIsInvalid(services.removeBenchmarksFromSpace(-1,TestUtil.getMockHttpRequest(
				admin.getId(),new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
	}
	
	@StarexecTest
	private void recycleBenchmarksTest() {
		assertResultIsInvalid(services.recycleBenchmarks(TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
		assertResultIsInvalid(services.recycleBenchmarks(TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
	}
	
	@StarexecTest
	private void restoreBenchmarksTest() {
		assertResultIsInvalid(services.restoreBenchmarks(TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
		assertResultIsInvalid(services.restoreBenchmarks(TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
	}
	
	@StarexecTest
	private void deleteBenchmarksTest() {
		assertResultIsInvalid(services.deleteBenchmarks(TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
		assertResultIsInvalid(services.deleteBenchmarks(TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
	}
	
	@StarexecTest
	private void recycleAndRemoveBenchmarksTest() {
		assertResultIsInvalid(services.recycleAndRemoveBenchmarks(space.getId(),TestUtil.getMockHttpRequest(
				user.getId(), new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
		assertResultIsInvalid(services.recycleAndRemoveBenchmarks(space.getId(),TestUtil.getMockHttpRequest(
				admin.getId(), new HashMap<String,String>())));
		assertResultIsInvalid(services.recycleAndRemoveBenchmarks(-1,TestUtil.getMockHttpRequest(
				admin.getId(),new HashMap<String,String>(), getSelectedIdParams(benchmarkIds.get(0), benchmarkIds.get(1)))));
	}
	
	@Override
	protected String getTestName() {
		return "RESTServicesSecurityTests";
	}

	@Override
	protected void setup() throws Exception {
		user = ResourceLoader.loadUserIntoDatabase();
		admin = Users.getAdmins().get(0);
		space = ResourceLoader.loadSpaceIntoDatabase(admin.getId(), 1);
		solver = ResourceLoader.loadSolverIntoDatabase(space.getId(), admin.getId());
		benchmarkIds = ResourceLoader.loadBenchmarksIntoDatabase(space.getId(), admin.getId());
		job = ResourceLoader.loadJobIntoDatabase(space.getId(), admin.getId(), solver.getId(), benchmarkIds);
		anonymousJobId = AnonymousLinks.addAnonymousLink("job", job.getId(), PrimitivesToAnonymize.NONE);
		benchmarkStatus = Uploads.getBenchmarkStatus(Uploads.createBenchmarkUploadStatus(space.getId(), admin.getId()));
		spaceStatus = Uploads.getSpaceXMLStatus(Uploads.createSpaceXMLUploadStatus(admin.getId()));
		Uploads.addFailedBenchmark(benchmarkStatus.getId(), "failed", "test message");
		invalidBenchId = Uploads.getFailedBenches(benchmarkStatus.getId()).get(0).getId();
		Websites.add(solver.getId(), "http://www.fakesite.com", "testsite", WebsiteType.SOLVER);
		solverWebsite = Websites.getAll(solver.getId(), WebsiteType.SOLVER).get(0);
		postProcessor = ResourceLoader.loadProcessorIntoDatabase(ProcessorType.POST, space.getId());
	}

	@Override
	protected void teardown() throws Exception {
		Solvers.deleteAndRemoveSolver(solver.getId());
		Jobs.deleteAndRemove(job.getId());
		for (int i : benchmarkIds) {
			Benchmarks.deleteAndRemoveBenchmark(i);
		}
		Spaces.removeSubspace(space.getId());
		Users.deleteUser(user.getId());
	}

}