/**
 * 
 */
package com.bangalorewest.bagtracker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.bangalorewest.bagtracker.constants.MessageBuilderConstants;
import com.bangalorewest.bagtracker.dto.LoadUnloadBag;
import com.bangalorewest.bagtracker.dto.PaxItinerary;
import com.bangalorewest.bagtracker.exception.InvalidBagTagException;

/**
 * @author sudhanshu.singh
 *
 */
@Component
public class MessageBuilder {

	@Autowired
	BookingDAO bookingDAO;

	public String buildBSM(PaxItinerary paxItinerary) {
		StringBuilder bsm = new StringBuilder();
		buildMsgType(bsm, true); // BSM or BPM
		buildVElement(paxItinerary, bsm); // .V element
		buildOnlyCurrentSegment(paxItinerary, bsm); // .F element
		buildOnlyOnwardSegments(paxItinerary, bsm); // .O element
		buildNElement(paxItinerary, bsm); // BagTag generation
		buildPElement(paxItinerary, bsm); // Pax Name
		buildEndMsg(bsm, true); // ENDBSM or ENDBPM
		return bsm.toString();

	}

	public String buildBPMForLoad(LoadUnloadBag loadUnloadBag) throws InvalidBagTagException {
		StringBuilder bpm = new StringBuilder();
		buildMsgType(bpm, false);
		List<PaxItinerary> paxItineraries = bookingDAO.getBookings(loadUnloadBag.getDateOfTravel());
		PaxItinerary validPaxItinerary = validateBagTagAndFilterItineraries(loadUnloadBag, paxItineraries);
		buildVElement(validPaxItinerary, bpm);
		buildOnlyCurrentSegment(validPaxItinerary, bpm);
		buildUElement(loadUnloadBag, bpm); // ULD Container
		buildNElement(validPaxItinerary, bpm);
		buildOnlyOnwardSegments(validPaxItinerary, bpm);
		buildPElement(validPaxItinerary, bpm);
		buildEndMsg(bpm, false);
		return bpm.toString();
	}

	private PaxItinerary validateBagTagAndFilterItineraries(LoadUnloadBag loadUnloadBag,
			List<PaxItinerary> paxItineraries) throws InvalidBagTagException {
		PaxItinerary validPaxItinerary = null;
		for (PaxItinerary paxItinerary : paxItineraries) {
			for (String bagTag : paxItinerary.getListOfGeneratedBagTags()) {
				if (bagTag.equalsIgnoreCase(loadUnloadBag.getBagTagID())
						&& paxItinerary.getDateOfTravel().equalsIgnoreCase(loadUnloadBag.getDateOfTravel())) {
					validPaxItinerary = paxItinerary;
				}
			}
		}
		if (validPaxItinerary == null) {
			throw new InvalidBagTagException("Invalid BagTag Passed in Load/Unload request");
		}
		return validPaxItinerary;
	}

	public String buildBPMForUnload(LoadUnloadBag loadUnloadRequest) throws InvalidBagTagException {
		StringBuilder bpm = new StringBuilder();
		buildMsgType(bpm, false);
		List<PaxItinerary> paxItineraries = bookingDAO.getBookings(loadUnloadRequest.getDateOfTravel());
		PaxItinerary validPaxItinerary = validateBagTagAndFilterItineraries(loadUnloadRequest, paxItineraries);

		buildVElement(validPaxItinerary, bpm);
		buildOnlyCurrentSegment(validPaxItinerary, bpm);
		buildUElement(loadUnloadRequest, bpm);
		buildBElement(validPaxItinerary, bpm);

		buildEndMsg(bpm, false);

		return bpm.toString();
	}

	private void buildBElement(PaxItinerary paxItinerary, StringBuilder bpm) {
		bpm.append(MessageBuilderConstants.DOT_B.getMessage()).append("OFF")
				.append(MessageBuilderConstants.FWD_SLASH.getMessage())
				.append(paxItinerary.getListOfGeneratedBagTags().get(0)).append("00")
				.append(paxItinerary.getNumberOfCheckedInBags()).append(System.lineSeparator());
	}

	private void buildUElement(LoadUnloadBag loadUnloadRequest, StringBuilder bpm) {
		bpm.append(MessageBuilderConstants.DOT_U.getMessage()).append(loadUnloadRequest.getUldContainer())
				.append(System.lineSeparator());
	}

	private void buildMsgType(StringBuilder sb, boolean isBSM) {
		if (isBSM) {
			sb.append(MessageBuilderConstants.START_BSM.getMessage());
		} else {
			sb.append(MessageBuilderConstants.START_BPM.getMessage());
		}
		sb.append(System.lineSeparator());
	}

	private void buildVElement(PaxItinerary paxItinerary, StringBuilder sb) {
		String[] splitSegments = splitSegments(paxItinerary);
		String[] firstSplitSegment = splitSegments[0].split("-");
		sb.append(MessageBuilderConstants.DOT_V.getMessage()).append(MessageBuilderConstants.LOCAL_BAG.getMessage())
				.append(firstSplitSegment[2]).append(System.lineSeparator());
	}

	private void buildOnlyCurrentSegment(PaxItinerary paxItinerary, StringBuilder sb) {
		sb.append(MessageBuilderConstants.DOT_F.getMessage());
		String[] splitSegments = splitSegments(paxItinerary);
		String[] segment = splitSegments[0].split("-");
		buildSegment(sb, segment);
	}

	private void buildOnlyOnwardSegments(PaxItinerary paxItinerary, StringBuilder sb) {
		String[] splitSegments = splitSegments(paxItinerary);
		for (int i = 1; i < splitSegments.length; i++) {
			sb.append(MessageBuilderConstants.DOT_O.getMessage());
			String[] segment = splitSegments[i].split("-");
			buildSegment(sb, segment);
		}
	}

	private String[] splitSegments(PaxItinerary paxItinerary) {
		String[] splitSegments = paxItinerary.getFlight().split(MessageBuilderConstants.HASH.getMessage());
		return splitSegments;
	}

	private void buildSegment(StringBuilder sb, String[] segment) {
		sb.append(segment[0]).append(MessageBuilderConstants.FWD_SLASH.getMessage()).append(segment[1].substring(0, 5))
				.append(MessageBuilderConstants.FWD_SLASH.getMessage()).append(segment[3])
				.append(MessageBuilderConstants.FWD_SLASH.getMessage())
				.append(MessageBuilderConstants.CABIN_CLASS_Y.getMessage()).append(System.lineSeparator());
	}

	private void buildNElement(PaxItinerary paxItinerary, StringBuilder sb) {
		int firstTag = generateAndSetBagTagsToPaxItineraryIfNotExist(paxItinerary);
		if (firstTag == 0) {
			sb.append(MessageBuilderConstants.DOT_N.getMessage())
					.append(paxItinerary.getListOfGeneratedBagTags().get(0)).append("00")
					.append(paxItinerary.getNumberOfCheckedInBags()).append(System.lineSeparator());
		}
		sb.append(MessageBuilderConstants.DOT_N.getMessage()).append(firstTag).append("00")
				.append(paxItinerary.getNumberOfCheckedInBags()).append(System.lineSeparator());
	}

	private int generateAndSetBagTagsToPaxItineraryIfNotExist(PaxItinerary paxItinerary) {
		int firstTag = 0;
		if (CollectionUtils.isEmpty(paxItinerary.getListOfGeneratedBagTags())) {
			List<String> generatedBagTags = new ArrayList<String>();
			Random r = new Random();
			firstTag = r.nextInt((100000 - 1000) + 1);
			int secondTag;
			generatedBagTags.add(String.valueOf(firstTag));
			if (paxItinerary.getNumberOfCheckedInBags() != null && paxItinerary.getNumberOfCheckedInBags() > 1) {
				secondTag = firstTag + 1;
				generatedBagTags.add(String.valueOf(secondTag));
			}
			paxItinerary.setListOfGeneratedBagTags(generatedBagTags);
		}
		return firstTag;
	}

	private void buildPElement(PaxItinerary paxItinerary, StringBuilder bsm) {
		bsm.append(MessageBuilderConstants.DOT_P.getMessage()).append(paxItinerary.getLastName())
				.append(MessageBuilderConstants.FWD_SLASH.getMessage()).append(paxItinerary.getFirstName())
				.append(System.lineSeparator());
	}

	private void buildEndMsg(StringBuilder msg, boolean isBSM) {
		if (isBSM) {
			msg.append(MessageBuilderConstants.END_BSM.getMessage());
		} else {
			msg.append(MessageBuilderConstants.END_BPM.getMessage());
		}

	}

}
