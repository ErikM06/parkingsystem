package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void testParkingACar() {

		Connection con = null;
		String requete = "SELECT COUNT(id) FROM ticket UNION SELECT SUM(AVAILABLE) FROM parking";
		int getIdAmountBeforeProcess = 0;
		int getIdAmountAfterProcess = 0;
		int getAvailableSumBeforeProcess = 0;
		int getAvailableSumAfterProcess = 0;

		try {
			con = dataBaseTestConfig.getConnection();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(requete);
			while (rs.next()) {
				getIdAmountBeforeProcess = rs.getInt(1);
				rs.next();
				getAvailableSumBeforeProcess = rs.getInt(1);

			}

			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.processIncomingVehicle();

			ResultSet rs2 = stmt.executeQuery(requete);

			while (rs2.next()) {
				getIdAmountAfterProcess = rs2.getInt(1);
				rs2.next();
				getAvailableSumAfterProcess = rs2.getInt(1);

			}
			assertTrue(getIdAmountAfterProcess > getIdAmountBeforeProcess
					&& getAvailableSumAfterProcess < getAvailableSumBeforeProcess);

		} catch (SQLException e) {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO: check that a ticket is actualy saved in DB and Parking table is updated
		// with availability
	}

	@Test
	public void testParkingLotExit() { // problemes si plusieurs ticket?
		Connection con = null;
		String priceRequete = "SELECT OUT_TIME FROM ticket UNION SELECT PRICE FROM ticket ORDER BY OUT_TIME DESC LIMIT 2";
		Double previousTicketFare = null;
		Double thisTicketFare = null;
		Timestamp thisOutTime = null;

		try {
			con = dataBaseTestConfig.getConnection();

			testParkingACar();

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(priceRequete);

			if (rs != null) {
				rs.absolute(1);
				previousTicketFare = rs.getDouble(1);
			}

			ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			parkingService.processExitingVehicle();

			stmt = con.createStatement();
			ResultSet rsNewTicket = stmt.executeQuery(priceRequete);

			if (rsNewTicket != null) {
				rsNewTicket.absolute(1);
				thisOutTime = rsNewTicket.getTimestamp(1);
				rsNewTicket.next();
				thisTicketFare = rsNewTicket.getDouble(1);
			}
			assertTrue(previousTicketFare != thisTicketFare && thisOutTime != null);

		} catch (SQLException e) {
		} catch (Exception e) {
		}
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

}
