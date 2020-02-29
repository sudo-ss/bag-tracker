/**
 * 
 */
package com.bangalorewest.bagtracker.service;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.bangalorewest.bagtracker.constants.MessageBuilderConstants;
import com.bangalorewest.bagtracker.dto.BagEvent;
import com.bangalorewest.bagtracker.dto.BagHistoriesFromBlockchain;
import com.bangalorewest.bagtracker.dto.BagHistoryDTO;
import com.bangalorewest.bagtracker.dto.BagHistoryFromBlockchain;
import com.bangalorewest.bagtracker.dto.BagStatusResponse;
import com.bangalorewest.bagtracker.dto.ConfirmLoginStatusDTO;
import com.bangalorewest.bagtracker.dto.ConfirmMessageGeneration;
import com.bangalorewest.bagtracker.dto.ConfirmSave;
import com.bangalorewest.bagtracker.dto.LoadUnloadBag;
import com.bangalorewest.bagtracker.dto.LoginDTO;
import com.bangalorewest.bagtracker.dto.PaxItinerary;
import com.bangalorewest.bagtracker.exception.AuthenticationFailureException;
import com.bangalorewest.bagtracker.exception.BagTagUpdateFailedException;
import com.bangalorewest.bagtracker.exception.InvalidBagTagException;
import com.bangalorewest.bagtracker.repository.BagAgentRepository;
import com.bangalorewest.bagtracker.repository.BookingRepository;
import com.bangalorewest.bagtracker.util.ConfigReader;
import com.bangalorewest.bagtracker.util.LoginHelper;

/**
 * @author sudhanshu.singh
 *
 */
@Service
public class BagTrackerServiceImpl implements BagTrackerService {

	@Autowired
	BagAgentRepository bagAgentRepository;

	@Autowired
	MessageBuilder msgBuilder;

	@Autowired
	BookingRepository bookingRepository;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public BagStatusResponse fetchLatestBagStatus(String bagTagID, String dateOfTravel) {
		// TODO Auto-generated method stub
		HttpHeaders headers = issueSecurityTokenAndSetHeaders();
		String URL = ConfigReader.getBaseUrl().concat(ConfigReader.getStatusApiUrl()).concat(bagTagID).concat("&")
				.concat("messageDate=").concat(dateOfTravel);
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		ResponseEntity<BagHistoriesFromBlockchain> resp = restTemplate.exchange(URL, HttpMethod.GET, entity,
				BagHistoriesFromBlockchain.class);
		BagStatusResponse bagStatus = createResponse(resp.getBody());
		return bagStatus;
	}

	private BagStatusResponse createResponse(BagHistoriesFromBlockchain bagHistoriesFromBlockchain) {
		// TODO Auto-generated method stub
		BagStatusResponse response = new BagStatusResponse();
		BagHistoryFromBlockchain bagHistoryFromBlockchain = bagHistoriesFromBlockchain.getData().get(0);
		response.setAirportCode(bagHistoryFromBlockchain.getAirportCode());
		response.setDate(bagHistoryFromBlockchain.getMessageDate());
		response.setStatus(findBagStatus(bagHistoryFromBlockchain));
		response.setFlight(findFlight(bagHistoryFromBlockchain));
		if (bagHistoryFromBlockchain.getMessage().contains("")) {

		}
		return null;
	}

	private String findFlight(BagHistoryFromBlockchain bagHistoryFromBlockchain) {
		// TODO Auto-generated method stub
		String message = bagHistoryFromBlockchain.getMessage();
//		message.
		return null;
	}

	private String findBagStatus(BagHistoryFromBlockchain bagHistoryFromBlockchain) {
		if (bagHistoryFromBlockchain.getMessage().contains(MessageBuilderConstants.START_BSM.getMessage())) {
			return "BAG CHECKEDIN";
		} else if (bagHistoryFromBlockchain.getMessage().contains(MessageBuilderConstants.DOT_U.getMessage())
				&& bagHistoryFromBlockchain.getMessage()
						.contains(MessageBuilderConstants.DOT_B.getMessage() + "/OFF")) {
			return "BAG OFFLOADED";
		} else if (bagHistoryFromBlockchain.getMessage().contains(MessageBuilderConstants.DOT_U.getMessage())) {
			return "BAG LOADED ONTO CONTAINER";
		} else if (bagHistoryFromBlockchain.getMessage().contains("HLD")) {
			return "BAG LOADED ONTO AIRCRAFT";
		}
		return null;
	}

	@Override
	public List<BagHistoryDTO> fetchBagHistory(String bagTagID, String dateOfTravel) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfirmMessageGeneration generateBSM(PaxItinerary paxItinerary) throws BagTagUpdateFailedException {
		String bsmMessage = msgBuilder.buildBSM(paxItinerary);
		String URL = ConfigReader.getBaseUrl().concat(ConfigReader.getPostBsm());
		HttpHeaders headers = issueSecurityTokenAndSetHeaders();
		if (paxItinerary.getNumberOfCheckedInBags() > 1) {
			for (String bagTagID : paxItinerary.getListOfGeneratedBagTags()) {
				BagEvent bagEvent = new BagEvent();
				bagEvent.setBagTagID(bagTagID);
				bagEvent.setBagDate(paxItinerary.getDateOfTravel());
				bagEvent.setFrom(paxItinerary.getLoggedInAgent());
				bagEvent.setMessage(bsmMessage.replaceAll(System.lineSeparator(), "||"));
				// persist each bagEvent to Blockchain

				String requestJSON = "{\"bagTagID\" : \"" + bagEvent.getBagTagID() + "\", \"from\" : \""
						+ paxItinerary.getLoggedInAgent() + "\", \"messageDate\" : \"" + bagEvent.getBagDate()
						+ "\", \"message\" : \"" + bagEvent.getMessage() + "\"}";
				System.out.println(requestJSON);
				HttpEntity<String> request = new HttpEntity<String>(requestJSON, headers);
				ResponseEntity<ConfirmSave> postForEntity = restTemplate.postForEntity(URL, request, ConfirmSave.class);
			}
		}

		updateGeneratedBagTagsToDB(paxItinerary);

		ConfirmMessageGeneration confirmMsg = new ConfirmMessageGeneration();
		confirmMsg.setMessage(bsmMessage);
		confirmMsg.setFrom(paxItinerary.getLoggedInAgent());
		return confirmMsg;

	}

	private HttpHeaders issueSecurityTokenAndSetHeaders() {
		String bearerToken = LoginHelper.issueBlockChainSecurityToken();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + bearerToken);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	@Override
	public ConfirmMessageGeneration generateLoadBPM(LoadUnloadBag loadUnloadBag) throws InvalidBagTagException {
		String bpmMessage = msgBuilder.buildBPMForLoad(loadUnloadBag);
		ConfirmMessageGeneration confirmMsg = new ConfirmMessageGeneration();
		confirmMsg.setMessage(bpmMessage);
		confirmMsg.setFrom(loadUnloadBag.getLoggedInAgent());

		// Call blockchain - persist

		return confirmMsg;
	}

	@Override
	public ConfirmMessageGeneration generateUnloadBPM(LoadUnloadBag loadUnloadRequest) throws InvalidBagTagException {
		// TODO Auto-generated method stub
		String bpmMessage = msgBuilder.buildBPMForUnload(loadUnloadRequest);
		ConfirmMessageGeneration confirmMsg = new ConfirmMessageGeneration();
		confirmMsg.setMessage(bpmMessage);
		confirmMsg.setFrom(loadUnloadRequest.getLoggedInAgent());

		return confirmMsg;
	}

	private void updateGeneratedBagTagsToDB(PaxItinerary paxItinerary) throws BagTagUpdateFailedException {
		String commaSeparatedBagTags = String.join(",", paxItinerary.getListOfGeneratedBagTags());
		Integer numberOfUpdatedRecords = bookingRepository.updateGeneratedBagTagsToBookings(commaSeparatedBagTags,
				paxItinerary.getEmailID(), paxItinerary.getFirstName());
		if (numberOfUpdatedRecords == null || numberOfUpdatedRecords == 0) {
			throw new BagTagUpdateFailedException("Couldn't update bagtags to DB");
		}
	}

	@Override
	public ConfirmLoginStatusDTO checkAndLogin(LoginDTO login) throws AuthenticationFailureException {
		List<Object> authenticatedUserDetails = null;
		if (Strings.isEmpty(login.getAgentLocation())) {
			try {
				authenticatedUserDetails = (List<Object>) bagAgentRepository.checkLogin(login.getUserPassword(),
						login.getUserName(), login.getUserRole());
			} catch (Exception e) {
				throw new AuthenticationFailureException("Login Failed");
			}
		} else {
			try {
				authenticatedUserDetails = (List<Object>) bagAgentRepository.checkLoginForLoadUnload(
						login.getUserPassword(), login.getUserName(), login.getUserRole(), login.getAgentLocation());
			} catch (Exception e) {
				throw new AuthenticationFailureException("Ground Handler login failed");
			}
		}
		if (!CollectionUtils.isEmpty(authenticatedUserDetails)) {
			return mapRetrievedDataToDTO(authenticatedUserDetails);
		} else {
			throw new AuthenticationFailureException("Agent Not Authorized");
		}
	}

	private ConfirmLoginStatusDTO mapRetrievedDataToDTO(List<Object> authenticatedUserDetails) {
		ConfirmLoginStatusDTO confirmLogin = null;
		Iterator itr = authenticatedUserDetails.iterator();
		while (itr.hasNext()) {
			Object[] obj = (Object[]) itr.next();
			String userName = String.valueOf(obj[0]);
			String userRole = String.valueOf(obj[1]);
			String agentLocation = String.valueOf(obj[2]);
			confirmLogin = new ConfirmLoginStatusDTO(userName, userRole, "Authentication Successful", agentLocation);
		}
		return confirmLogin;

	}

}
