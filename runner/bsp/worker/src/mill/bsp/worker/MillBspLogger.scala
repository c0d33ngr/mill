package mill.bsp.worker

import ch.epfl.scala.bsp4j._
import mill.api.Logger
import mill.api.ProxyLogger

/**
 * BSP-specialized logger class which sends `task-progress`
 * notifications ( upon the invocation of the `ticker` method ) and
 * `show-message` notifications ( for each error or information
 * being logged ).
 *
 * @param client the client to send notifications to, also the
 *               client that initiated a request which triggered
 *               a mill task evaluation
 * @param taskId unique ID of the task being evaluated
 * @param logger the logger to which the messages received by this
 *               MillBspLogger are being redirected
 */
private class MillBspLogger(client: BuildClient, taskId: Int, logger: Logger)
    extends ProxyLogger(logger)
    with Logger {

  override def ticker(s: String): Unit = {
    try {
      val progressString = s.split(" ")(0)
      val progress = progressString.substring(1, progressString.length - 1).split("/")
      val params = new TaskProgressParams(new TaskId(taskId.toString))
      params.setEventTime(System.currentTimeMillis())
      params.setMessage(s)
      params.setUnit(s.split(" ")(1))
      params.setProgress(progress(0).toLong)
      params.setTotal(progress(1).toLong)
      client.onBuildTaskProgress(params)
      super.ticker(s)
    } catch {
      case _: Exception => // noop
    }
  }
}
