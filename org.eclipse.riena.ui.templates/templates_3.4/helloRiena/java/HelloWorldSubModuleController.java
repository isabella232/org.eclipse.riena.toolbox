package $packageName$;

import org.eclipse.riena.navigation.ISubModuleNode;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.ILabelRidget;

public class HelloWorldSubModuleController extends SubModuleController {

	private ILabelRidget labelRidget;
	private TxtBean bean;

	public HelloWorldSubModuleController(ISubModuleNode navigationNode) {
		super(navigationNode);
		bean = new TxtBean();
		bean.setTxt("Hello World");
		bean.setName("");
	}

	/**
	 * @see org.eclipse.riena.navigation.ui.controllers.SubModuleNodeViewController#afterBind()
	 */
	@Override
	public void afterBind() {
		super.afterBind();
		initLabelRidget();
	}

	/**
	 * Binds and updates the label.
	 */
	private void initLabelRidget() {
		if (labelRidget != null) {
			labelRidget.bindToModel(bean, "txt");
			labelRidget.updateFromModel();
		}
	}

	/**
	 * The model of this sub module controller.
	 */
	private class TxtBean {

		private String txt;
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTxt() {
			return txt;
		}

		public void setTxt(String txt) {
			this.txt = txt;
		}

	}

	public ILabelRidget getLabelRidget() {
		return labelRidget;
	}

	public void setLabelRidget(ILabelRidget labelRidget) {
		this.labelRidget = labelRidget;
	}

}
