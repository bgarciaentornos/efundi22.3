/**
 * Copyright (c) 2003-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.gradebookng.tool.panels;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.sakaiproject.gradebookng.business.GbCategoryType;
import org.sakaiproject.gradebookng.business.GbRole;
import org.sakaiproject.gradebookng.business.model.GbUser;
import org.sakaiproject.gradebookng.business.util.CourseGradeFormatter;
import org.sakaiproject.gradebookng.business.util.FormatHelper;
import org.sakaiproject.gradebookng.tool.component.GbAjaxButton;
import org.sakaiproject.gradebookng.tool.component.GbFeedbackPanel;
import org.sakaiproject.service.gradebook.shared.CourseGrade;
import org.sakaiproject.service.gradebook.shared.GradebookInformation;
import org.sakaiproject.tool.gradebook.Gradebook;

/**
 * Panel for the course grade override window
 *
 * Note that validation is disabled when final grade mode is activated.
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 */
public class CourseGradeOverridePanel extends BasePanel {

	private static final long serialVersionUID = 1L;

	private final ModalWindow window;

	public CourseGradeOverridePanel(final String id, final IModel<String> model, final ModalWindow window) {
		super(id, model);
		this.window = window;
	}

	@Override
	public void onInitialize() {
		super.onInitialize();

		// unpack model
		final String studentUuid = (String) getDefaultModelObject();

		// get the rest of the data we need
		// TODO this could all be passed in through the model if it was changed to a map, as per CourseGradeItemCellPanel...
		final GbUser studentUser = this.businessService.getUser(studentUuid);
		final String currentUserUuid = getCurrentUserId();
		final Locale currentUserLocale = this.businessService.getUserPreferredLocale();
		final GbRole currentUserRole = getUserRole();
		final Gradebook gradebook = getGradebook();
		final boolean courseGradeVisible = this.businessService.isCourseGradeVisible(currentUserUuid);

		final CourseGrade courseGrade = this.businessService.getCourseGrade(studentUuid);
		final CourseGradeFormatter courseGradeFormatter = new CourseGradeFormatter(
				gradebook,
				currentUserRole,
				courseGradeVisible,
				false,
				false,
				this.businessService.getShowCalculatedGrade());

		// heading
		CourseGradeOverridePanel.this.window.setTitle(
				(new StringResourceModel("heading.coursegrade", null,
						new Object[] { studentUser.getDisplayName(), studentUser.getDisplayId() })).getString());

		// form model
		// we are only dealing with the 'entered grade' so we use this directly
		final Model<String> formModel = new Model<String>(courseGrade.getEnteredGrade());

		// form
		final Form<String> form = new Form<String>("form", formModel);

		form.add(new Label("studentName", studentUser.getDisplayName()));
		form.add(new Label("studentEid", studentUser.getDisplayId()));
		form.add(new Label("points", formatPoints(courseGrade, gradebook)));
		form.add(new Label("calculated", courseGradeFormatter.format(courseGrade)));

		final TextField<String> overrideField = new TextField<>("overrideGrade", formModel);
		overrideField.setOutputMarkupId(true);
		form.add(overrideField);

		final GbAjaxButton submit = new GbAjaxButton("submit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
				String newGrade = (String) form.getModelObject();
				String gradeScale = null;

				// validate the grade entered is a valid one for the selected grading schema
				// though we allow blank grades so the override is removed
				// Note: validation is not enforced for final grade mode
				final GradebookInformation gbInfo = CourseGradeOverridePanel.this.businessService.getGradebookSettings();

				if (StringUtils.isNotBlank(newGrade)) {
					final Map<String, Double> schema = gbInfo.getSelectedGradingScaleBottomPercents();
					
					if (!schema.containsKey(newGrade)) {
						try {
							gradeScale = FormatHelper.getGradeFromNumber(newGrade, schema, currentUserLocale);
							newGrade = FormatHelper.transformNewGrade(newGrade, currentUserLocale);
						}
						catch (NumberFormatException e)	{
							error(new ResourceModel("message.addcoursegradeoverride.invalid").getObject());
							target.addChildren(form, FeedbackPanel.class);
							return;
						}
					} else {
						gradeScale = newGrade;
						newGrade = FormatHelper.getNumberFromGrade(gradeScale, schema, currentUserLocale);
					}
				}

				// save
				final boolean success = CourseGradeOverridePanel.this.businessService.updateCourseGrade(studentUuid, newGrade, gradeScale);

				if (success) {
					getSession().success(getString("message.addcoursegradeoverride.success"));
					setResponsePage(getPage().getPageClass());
				} else {
					error(new ResourceModel("message.addcoursegradeoverride.error").getObject());
					target.addChildren(form, FeedbackPanel.class);
				}

			}
		};
		form.add(submit);

		// feedback panel
		form.add(new GbFeedbackPanel("feedback"));

		// cancel button
		final GbAjaxButton cancel = new GbAjaxButton("cancel") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(final AjaxRequestTarget target, final Form<?> f) {
				CourseGradeOverridePanel.this.window.close(target);
			}
		};
		cancel.setDefaultFormProcessing(false);
		form.add(cancel);

		// revert link
		final AjaxSubmitLink revertLink = new AjaxSubmitLink("revertOverride", form) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(final AjaxRequestTarget target, final Form<?> f) {
				final boolean success = CourseGradeOverridePanel.this.businessService.updateCourseGrade(studentUuid, null, null);
				if (success) {
					getSession().success(getString("message.addcoursegradeoverride.success"));
					setResponsePage(getPage().getPageClass());
				} else {
					error(new ResourceModel("message.addcoursegradeoverride.error").getObject());
					target.addChildren(form, FeedbackPanel.class);
				}
			}

			@Override
			public boolean isVisible() {
				return StringUtils.isNotBlank(formModel.getObject());
			}
		};
		revertLink.setDefaultFormProcessing(false);
		form.add(revertLink);

		add(form);
	}

	/**
	 * Helper to format the points display
	 *
	 * @param courseGrade the {@link CourseGrade}
	 * @param gradebook the {@link Gradebook} which has the settings
	 * @return fully formatted string ready for display
	 */
	private String formatPoints(final CourseGrade courseGrade, final Gradebook gradebook) {

		String rval;

		// only display points if not weighted category type
		final GbCategoryType categoryType = GbCategoryType.valueOf(gradebook.getCategory_type());
		if (categoryType != GbCategoryType.WEIGHTED_CATEGORY) {

			final Double pointsEarned = courseGrade.getPointsEarned();
			final Double totalPointsPossible = courseGrade.getTotalPointsPossible();

			if (pointsEarned != null && totalPointsPossible != null) {
				rval = new StringResourceModel("coursegrade.display.points-first", null,
						new Object[] { pointsEarned, totalPointsPossible }).getString();
			} else {
				rval = getString("coursegrade.display.points-none");
			}
		} else {
			rval = getString("coursegrade.display.points-none");
		}

		return rval;

	}

}
