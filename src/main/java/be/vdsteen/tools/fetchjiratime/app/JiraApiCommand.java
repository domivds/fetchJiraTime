package be.vdsteen.tools.fetchjiratime.app;

public interface JiraApiCommand extends Runnable {
  void validate();
}
