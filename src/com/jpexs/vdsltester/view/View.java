package com.jpexs.vdsltester.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.jpexs.vdsltester.R;
import com.jpexs.vdsltester.model.Main;
import com.jpexs.vdsltester.model.Router;
import com.jpexs.vdsltester.model.RouterMeasurement;
import com.jpexs.vdsltester.model.routers.ComtrendRouter;
import com.jpexs.vdsltester.view.language.CzechLanguage;
import com.jpexs.vdsltester.view.language.CzechNoDiaLanguage;
import com.jpexs.vdsltester.view.language.Language;

/**
 * 
 * @author JPEXS
 */
public class View {
	// public MainForm mainForm;
	public Language language;
	public double zoom = 2;
	// public ConnectionForm connectionForm;

	public static final List<Language> availableLanguages = new ArrayList<Language>();
	public static String langId = "en";
	public ConnectionSettingsActivity connectionActivity;
	public MainActivity mainActivity;
	public Activity currentActivity;
	private String selectedCard;
	private android.view.View currentView;

	static {
		availableLanguages.add(new Language());
		availableLanguages.add(new CzechLanguage());
		availableLanguages.add(new CzechNoDiaLanguage());
	}

	public void setView(android.view.View currentView) {
		this.currentView = currentView;
	}

	public View() {
		for (int i = 0; i < availableLanguages.size(); i++) {
			Language lang = availableLanguages.get(i);
			if (lang.langId.equals(langId)) {
				language = lang;
				break;
			}
		}
		if (language == null) {
			language = availableLanguages.get(0);
		}
	}

	public void showConfig() {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getSelectedCard() {
		return selectedCard;
	}

	public void initDisplay() {

	}

	public void showMain() {
		throw new UnsupportedOperationException("not implemented");
	}

	public void setCard(String cardName) {
		selectedCard = cardName;
	}

	public HashSet<String> getNeededFields(String cardName) {
		HashSet<String> ret = new HashSet<String>();
		if (cardName == null) {
			return ret;
		}
		ret.add("status");

		if (cardName.equals("HLOG")) {
			ret.add("graphHlog");
		}
		if (cardName.equals("INFO")) {
			ret.add("name");
			ret.add("mode");
			ret.add("type");
			ret.add("profile");
			ret.add("wanIP");
			ret.add("SWVersion");
			ret.add("upTime");
			ret.add("linkTime");
			ret.add("reconnect");
			ret.add("max_rate");
			ret.add("actual_rate");
			ret.add("power");
			ret.add("snr");
			ret.add("inp");
			ret.add("delay");

			// params
			ret.add("band_latn");
			ret.add("band_satn");
			ret.add("band_margin");
			ret.add("band_power");
		}
		if (cardName.equals("1DAY") || cardName.equals("15MIN")) {
			ret.add("errors");
		}
		if (cardName.equals("GRAPHBIT")) {
			ret.add("graphBits");
			ret.add("band_final_plan");
		}
		if (cardName.equals("GRAPHSNR")) {
			ret.add("graphSNR");
			ret.add("band_final_plan");
		}
		if (cardName.equals("GRAPHQLN")) {
			ret.add("graphQLN");
			ret.add("band_final_plan");
		}
		if (cardName.equals("GRAPHHLOG")) {
			ret.add("graphHlog");
			ret.add("band_final_plan");
		}
		return ret;
	}

	private void updateText(int id, final String value) {
		if(currentView==null){
			return;
		}
		final TextView tw = (TextView) currentView.findViewById(id);

		if (tw != null) {
			tw.post(new Runnable() {
				public void run() {
					tw.setText(value);
				}
			});
		}

	}

	public void updateFields(final RouterMeasurement rm, String cardName) {
		// TODO: update components
		// System.out.println(rm.toString());
		final TextView tw = (TextView) currentActivity
				.findViewById(R.id.errorTextView);
		tw.post(new Runnable() {
			public void run() {
				tw.setText("");
			}
		});
		if (cardName.equals("INFO")) {
			updateText(R.id.modemTextView, rm.name);
			updateText(R.id.modeTextView, rm.mode);
			updateText(R.id.profileTextView, rm.profile);
			updateText(R.id.typeTextView, rm.type);
			updateText(R.id.wanIPTextView, rm.wanIP);
			updateText(R.id.SWVersionTextView, rm.SWVersion);
			updateText(R.id.reconnectTextView, rm.reconnect);
			updateText(R.id.upTimeTextView, rm.upTime);
			updateText(R.id.linkTimeTextView, rm.linkTime);

			updateText(R.id.currentSpeedUSTextView, rm.US_actual_rate);
			updateText(R.id.maxSpeedUSTextView, rm.US_max_rate);
			updateText(R.id.snrmUSTextView, rm.US_snr);
			updateText(R.id.delayUSTextView, rm.US_delay);
			updateText(R.id.inpUSTextView, rm.US_inp);
			updateText(R.id.powerUSTextView, rm.US_power);

			updateText(R.id.currentSpeedDSTextView, rm.DS_actual_rate);
			updateText(R.id.maxSpeedDSTextView, rm.DS_max_rate);
			updateText(R.id.snrmDSTextView, rm.DS_snr);
			updateText(R.id.delayDSTextView, rm.DS_delay);
			updateText(R.id.inpDSTextView, rm.DS_inp);
			updateText(R.id.powerDSTextView, rm.DS_power);

			updateText(R.id.latnU0TextView, rm.U0_latn);
			updateText(R.id.satnU0TextView, rm.U0_satn);
			updateText(R.id.snrmU0TextView, rm.U0_snr);
			updateText(R.id.powerU0TextView, rm.U0_power);

			updateText(R.id.latnU1TextView, rm.U1_latn);
			updateText(R.id.satnU1TextView, rm.U1_satn);
			updateText(R.id.snrmU1TextView, rm.U1_snr);
			updateText(R.id.powerU1TextView, rm.U1_power);

			updateText(R.id.latnU2TextView, rm.U2_latn);
			updateText(R.id.satnU2TextView, rm.U2_satn);
			updateText(R.id.snrmU2TextView, rm.U2_snr);
			updateText(R.id.powerU2TextView, rm.U2_power);

			updateText(R.id.latnU3TextView, rm.U3_latn);
			updateText(R.id.satnU3TextView, rm.U3_satn);
			updateText(R.id.snrmU3TextView, rm.U3_snr);
			updateText(R.id.powerU3TextView, rm.U3_power);

			updateText(R.id.latnD1TextView, rm.D1_latn);
			updateText(R.id.satnD1TextView, rm.D1_satn);
			updateText(R.id.snrmD1TextView, rm.D1_snr);
			updateText(R.id.powerD1TextView, rm.D1_power);

			updateText(R.id.latnD2TextView, rm.D2_latn);
			updateText(R.id.satnD2TextView, rm.D2_satn);
			updateText(R.id.snrmD2TextView, rm.D2_snr);
			updateText(R.id.powerD2TextView, rm.D2_power);

			updateText(R.id.latnD3TextView, rm.D3_latn);
			updateText(R.id.satnD3TextView, rm.D3_satn);
			updateText(R.id.snrmD3TextView, rm.D3_snr);
			updateText(R.id.powerD3TextView, rm.D3_power);

		}

		if (cardName.equals("1DAY")) {
			updateText(R.id.SUMAErrorsLinkTimeTextView, rm.errorsAll.detail);
			updateText(R.id.usESLinkTimeTextView, rm.errorsAll.US_ES);
			updateText(R.id.usUASLinkTimeTextView, rm.errorsAll.US_UAS);
			updateText(R.id.usCRCLinkTimeTextView, rm.errorsAll.US_CRC);
			updateText(R.id.usFECLinkTimeTextView, rm.errorsAll.US_FEC);
			updateText(R.id.dsESLinkTimeTextView, rm.errorsAll.DS_ES);
			updateText(R.id.dsUASLinkTimeTextView, rm.errorsAll.DS_UAS);
			updateText(R.id.dsCRCLinkTimeTextView, rm.errorsAll.DS_CRC);
			updateText(R.id.dsFECLinkTimeTextView, rm.errorsAll.DS_FEC);

			updateText(R.id.SUMAErrorsLatest1DayTimeTextView,
					rm.errorsLatest1Day.detail);
			updateText(R.id.usESLatest1DayTextView, rm.errorsLatest1Day.US_ES);
			updateText(R.id.usUASLatest1DayTextView, rm.errorsLatest1Day.US_UAS);
			updateText(R.id.usCRCLatest1DayTextView, rm.errorsLatest1Day.US_CRC);
			updateText(R.id.usFECLatest1DayTextView, rm.errorsLatest1Day.US_FEC);
			updateText(R.id.dsESLatest1DayTextView, rm.errorsLatest1Day.DS_ES);
			updateText(R.id.dsUASLatest1DayTextView, rm.errorsLatest1Day.DS_UAS);
			updateText(R.id.dsCRCLatest1DayTextView, rm.errorsLatest1Day.DS_CRC);
			updateText(R.id.dsFECLatest1DayTextView, rm.errorsLatest1Day.DS_FEC);

			updateText(R.id.SUMAErrorsPrevious1DayTimeTextView,
					rm.errorsPrevious1Day.detail);
			updateText(R.id.usESPrevious1DayTextView,
					rm.errorsPrevious1Day.US_ES);
			updateText(R.id.usUASPrevious1DayTextView,
					rm.errorsPrevious1Day.US_UAS);
			updateText(R.id.usCRCPrevious1DayTextView,
					rm.errorsPrevious1Day.US_CRC);
			updateText(R.id.usFECPrevious1DayTextView,
					rm.errorsPrevious1Day.US_FEC);
			updateText(R.id.dsESPrevious1DayTextView,
					rm.errorsPrevious1Day.DS_ES);
			updateText(R.id.dsUASPrevious1DayTextView,
					rm.errorsPrevious1Day.DS_UAS);
			updateText(R.id.dsCRCPrevious1DayTextView,
					rm.errorsPrevious1Day.DS_CRC);
			updateText(R.id.dsFECPrevious1DayTextView,
					rm.errorsPrevious1Day.DS_FEC);
		}
		if (cardName.equals("15MIN")) {
			updateText(R.id.SUMAErrorsLinkTimeTextView, rm.errorsAll.detail);
			updateText(R.id.usESLinkTimeTextView, rm.errorsAll.US_ES);
			updateText(R.id.usUASLinkTimeTextView, rm.errorsAll.US_UAS);
			updateText(R.id.usCRCLinkTimeTextView, rm.errorsAll.US_CRC);
			updateText(R.id.usFECLinkTimeTextView, rm.errorsAll.US_FEC);
			updateText(R.id.dsESLinkTimeTextView, rm.errorsAll.DS_ES);
			updateText(R.id.dsUASLinkTimeTextView, rm.errorsAll.DS_UAS);
			updateText(R.id.dsCRCLinkTimeTextView, rm.errorsAll.DS_CRC);
			updateText(R.id.dsFECLinkTimeTextView, rm.errorsAll.DS_FEC);

			updateText(R.id.SUMAErrorsLatest15MinutesTimeTextView,
					rm.errorsLatest15Min.detail);
			updateText(R.id.usESLatest15MinutesTextView,
					rm.errorsLatest15Min.US_ES);
			updateText(R.id.usUASLatest15MinutesTextView,
					rm.errorsLatest15Min.US_UAS);
			updateText(R.id.usCRCLatest15MinutesTextView,
					rm.errorsLatest15Min.US_CRC);
			updateText(R.id.usFECLatest15MinutesTextView,
					rm.errorsLatest15Min.US_FEC);
			updateText(R.id.dsESLatest15MinutesTextView,
					rm.errorsLatest15Min.DS_ES);
			updateText(R.id.dsUASLatest15MinutesTextView,
					rm.errorsLatest15Min.DS_UAS);
			updateText(R.id.dsCRCLatest15MinutesTextView,
					rm.errorsLatest15Min.DS_CRC);
			updateText(R.id.dsFECLatest15MinutesTextView,
					rm.errorsLatest15Min.DS_FEC);

			updateText(R.id.SUMAErrorsPrevious15MinutesTimeTextView,
					rm.errorsPrevious15Min.detail);
			updateText(R.id.usESPrevious15MinutesTextView,
					rm.errorsPrevious15Min.US_ES);
			updateText(R.id.usUASPrevious15MinutesTextView,
					rm.errorsPrevious15Min.US_UAS);
			updateText(R.id.usCRCPrevious15MinutesTextView,
					rm.errorsPrevious15Min.US_CRC);
			updateText(R.id.usFECPrevious15MinutesTextView,
					rm.errorsPrevious15Min.US_FEC);
			updateText(R.id.dsESPrevious15MinutesTextView,
					rm.errorsPrevious15Min.DS_ES);
			updateText(R.id.dsUASPrevious15MinutesTextView,
					rm.errorsPrevious15Min.DS_UAS);
			updateText(R.id.dsCRCPrevious15MinutesTextView,
					rm.errorsPrevious15Min.DS_CRC);
			updateText(R.id.dsFECPrevious15MinutesTextView,
					rm.errorsPrevious15Min.DS_FEC);
		}
		if (cardName.equals("HLOG")) {
			if (rm.graphHlog != null) {
				if (rm.graphHlog.size() > 72) {
					updateText(R.id.hlog72, "" + rm.graphHlog.get(72));
				} else {
					updateText(R.id.hlog72, null);
				}
				if (rm.graphHlog.size() > 232) {
					updateText(R.id.hlog232, "" + rm.graphHlog.get(232));
				} else {
					updateText(R.id.hlog232, null);
				}
				if (rm.graphHlog.size() > 812) {
					updateText(R.id.hlog812, "" + rm.graphHlog.get(812));
				} else {
					updateText(R.id.hlog812, null);
				}
				if (rm.graphHlog.size() > 1275) {
					updateText(R.id.hlog1275, "" + rm.graphHlog.get(1275));
				} else {
					updateText(R.id.hlog1275, null);
				}
				if (rm.graphHlog.size() > 1855) {
					updateText(R.id.hlog1855, "" + rm.graphHlog.get(1855));
				} else {
					updateText(R.id.hlog1855, null);
				}
				if (rm.graphHlog.size() > 2899) {
					updateText(R.id.hlog2899, "" + rm.graphHlog.get(2899));
				} else {
					updateText(R.id.hlog2899, null);
				}
				if (rm.graphHlog.size() > 3710) {
					updateText(R.id.hlog3710, "" + rm.graphHlog.get(3710));
				} else {
					updateText(R.id.hlog3710, null);
				}
			} else {
				updateText(R.id.hlog72, null);
				updateText(R.id.hlog232, null);
				updateText(R.id.hlog812, null);
				updateText(R.id.hlog1275, null);
				updateText(R.id.hlog1855, null);
				updateText(R.id.hlog2899, null);
				updateText(R.id.hlog3710, null);
			}
		}

		if (cardName.equals("GRAPHBIT")) {
			final GraphView gw = (GraphView) currentView
					.findViewById(R.id.graphBit);
			if (gw != null) {
				tw.post(new Runnable() {
					public void run() {
						gw.setValues(rm.graphBits, rm.USbandPlanFinal,
								rm.DSbandPlanFinal);
					}
				});
			}
		}
		if (cardName.equals("GRAPHSNR")) {
			final GraphView gw = (GraphView) currentView
					.findViewById(R.id.graphSnr);
			if (gw != null) {
				tw.post(new Runnable() {
					public void run() {
						gw.setValues(rm.graphSNR, rm.USbandPlanFinal,
								rm.DSbandPlanFinal);
					}
				});
			}
		}
		if (cardName.equals("GRAPHQLN")) {

			final GraphView gw = (GraphView) currentView
					.findViewById(R.id.graphQln);
			if (gw != null) {
				tw.post(new Runnable() {
					public void run() {
						gw.setValues(rm.graphQLN, rm.USbandPlanFinal,
								rm.DSbandPlanFinal);
					}
				});
			}
		}
		if (cardName.equals("GRAPHHLOG")) {
			final GraphView gw = (GraphView) currentView
					.findViewById(R.id.graphHlog);
			if (gw != null) {
				tw.post(new Runnable() {
					public void run() {
						gw.setValues(rm.graphHlog, rm.USbandPlanFinal,
								rm.DSbandPlanFinal);
					}
				});
			}
		}
	}

	public boolean areParametersValid() {
		throw new UnsupportedOperationException("not implemented");
	}

	public int getPort() {
		return 23;
	}

	public String getAddress() {
		return "10.0.0.138";
	}

	public void displayMessageInvalid() {
		throw new UnsupportedOperationException("not implemented");
	}

	public Router getRouter() {
		return new ComtrendRouter();
	}

	public void hideConfig() {
		throw new UnsupportedOperationException("not implemented");
	}

	public void switchAdvancedConfig() {
		throw new UnsupportedOperationException("not implemented");
	}

	public String getConnectionUserName() {
		return "admin";
	}

	public String getConnectionPassword() {
		return "admin";
	}
}
