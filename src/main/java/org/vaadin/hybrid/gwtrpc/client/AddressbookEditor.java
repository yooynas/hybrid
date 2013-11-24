package org.vaadin.hybrid.gwtrpc.client;

import java.util.ArrayList;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;

public class AddressbookEditor extends Composite {

	VerticalPanel layout = new VerticalPanel();
	DataGrid<AddressTO> addressList = new DataGrid<AddressTO>();
	SingleSelectionModel<AddressTO> selectionModel;
	HorizontalPanel tableActions = new HorizontalPanel();
	Button editButton = new Button("Edit");
	Button deleteButton = new Button("Delete");
	Button newButton = new Button("New");
	FlowPanel form = new FlowPanel();
	TextBox firstName = new TextBox();
	TextBox lastName = new TextBox();
	TextBox phoneNumber = new TextBox();
	TextBox emailAddress = new TextBox();
	HorizontalPanel formActions = new HorizontalPanel();
	Button saveButton = new Button("Save");
	Button cancelButton = new Button("Cancel");
	AddressbookRPCServiceAsync service = GWT
			.create(AddressbookRPCService.class);

	private static final String CONNECTION_ERROR = "Could not connect to server";

	public AddressbookEditor() {
		initLayout();
		initWidget(layout);
		initAddressList();
		updateAddressList();
		initAddressListActions();
		initForm();
	}

	private void initLayout() {
		layout.setWidth("100%");
		layout.add(addressList);
		layout.add(tableActions);
		layout.add(form);
		layout.add(formActions);

		addressList.setWidth("600px"); // TODO 100% width
		addressList.setHeight("320px"); // TODO 15 lines high

		tableActions.add(editButton);
		tableActions.add(deleteButton);
		tableActions.add(newButton);
		// TODO layout.setComponentAlignment(tableActions,
		// Alignment.MIDDLE_RIGHT);

		form.setWidth("100%");
		form.add(new Label("First Name"));
		form.add(firstName);
		form.add(new Label("Last Name"));
		form.add(lastName);
		form.add(new Label("Phone Number"));
		form.add(phoneNumber);
		form.add(new Label("Email Address"));
		form.add(emailAddress);

		firstName.setWidth("100%");
		lastName.setWidth("100%");
		phoneNumber.setWidth("50%");
		emailAddress.setWidth("70%");

		formActions.add(saveButton);
		formActions.add(cancelButton);
		// TODO layout.setComponentAlignment(formActions,
		// Alignment.MIDDLE_RIGHT);
	}

	private void initAddressList() {

		// Add a selection model so we can select rows
		selectionModel = new SingleSelectionModel<AddressTO>(
				new ProvidesKey<AddressTO>() {
					public Object getKey(AddressTO item) {
						return item.getId();
					}
				});
		addressList.setSelectionModel(selectionModel);

		// First name.
		Column<AddressTO, String> firstNameColumn = new Column<AddressTO, String>(
				new TextCell()) {
			@Override
			public String getValue(AddressTO a) {
				return a.getFirstName();
			}
		};
		addressList.addColumn(firstNameColumn,
				SafeHtmlUtils.fromSafeConstant("First Name"));
		// addressList.setColumnWidth(firstNameColumn, 20, Unit.PCT);

		// First name.
		Column<AddressTO, String> lastNameColumn = new Column<AddressTO, String>(
				new TextCell()) {
			@Override
			public String getValue(AddressTO a) {
				return a.getLastName();
			}
		};
		addressList.addColumn(lastNameColumn,
				SafeHtmlUtils.fromSafeConstant("Last Name"));
		// addressList.setColumnWidth(lastNameColumn, 20, Unit.PCT);

		// Email Address
		Column<AddressTO, String> emailAddressColumn = new Column<AddressTO, String>(
				new TextCell()) {
			@Override
			public String getValue(AddressTO a) {
				return a.getEmailAddress();
			}
		};
		addressList.addColumn(emailAddressColumn,
				SafeHtmlUtils.fromSafeConstant("Email"));
		// addressList.setColumnWidth(emailAddressColumn, 20, Unit.PCT);

		// Phone number
		Column<AddressTO, String> phoneNumberColumn = new Column<AddressTO, String>(
				new TextCell()) {
			@Override
			public String getValue(AddressTO a) {
				return a.getPhoneNumber();
			}
		};
		addressList.addColumn(phoneNumberColumn,
				SafeHtmlUtils.fromSafeConstant("Phone"));
		// addressList.setColumnWidth(phoneNumberColumn, 20, Unit.PCT);
	}

	private void updateAddressList() {

		service.getAddressess(new AsyncCallback<AddressTO[]>() {
			public void onSuccess(AddressTO[] result) {
				addressList.setRowCount(result.length);
				ArrayList<AddressTO> rowData = new ArrayList<AddressTO>(
						result.length);
				for (int i = 0; i < result.length; i++)
					rowData.add(result[i]);
				addressList.setRowData(rowData);

			}

			public void onFailure(Throwable caught) {
				Window.alert(CONNECTION_ERROR);
			}
		});
	}

	private void initAddressListActions() {

		editButton.addClickHandler(new ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				AddressTO a = selectionModel.getSelectedObject();
				if (a != null)
					editAddress(a);
			}
		});

		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				AddressTO a = selectionModel.getSelectedObject();
				if (a != null) {
					service.deleteAddress(a.getId(),
							new AsyncCallback<Object>() {
								public void onFailure(Throwable caught) {
									Window.alert(CONNECTION_ERROR);
								}

								public void onSuccess(Object result) {
									updateAddressList();
								}
							});
				}
			}
		});

		newButton.addClickHandler(new ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				AddressTO a = selectionModel.getSelectedObject();
				if (a != null) {
					service.newAddress(new AsyncCallback<AddressTO>() {
						public void onFailure(Throwable caught) {
							Window.alert(CONNECTION_ERROR);
						}

						public void onSuccess(AddressTO result) {
							editAddress(result);
							// TODO select the result at addressList
						}
					});
				}
			}
		});

	}

	private void updateActionVisibility(boolean editingAddress) {
		// TODO update component enabled states
		// TODO ensure that this method is called in appropriate events
	}

	private void initForm() {

		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				AddressTO a = new AddressTO();
				a.setId(selectionModel.getSelectedObject().getId());
				a.setFirstName(firstName.getValue());
				a.setLastName(lastName.getValue());
				a.setPhoneNumber(phoneNumber.getValue());
				a.setEmailAddress(emailAddress.getValue());
				service.storeAddress(a, new AsyncCallback<Object>() {
					public void onFailure(Throwable caught) {
						Window.alert(CONNECTION_ERROR);
					}

					public void onSuccess(Object result) {
						updateAddressList();
						// TODO reselect the value in addressList
						firstName.setValue("");
						lastName.setValue("");
						emailAddress.setValue("");
						phoneNumber.setValue("");
					}
				});
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				firstName.setValue("");
				lastName.setValue("");
				emailAddress.setValue("");
				phoneNumber.setValue("");
			}
		});

	}

	private void editAddress(AddressTO a) {
		firstName.setValue(a.getFirstName());
		lastName.setValue(a.getLastName());
		emailAddress.setValue(a.getEmailAddress());
		phoneNumber.setValue(a.getPhoneNumber());
	}

}
