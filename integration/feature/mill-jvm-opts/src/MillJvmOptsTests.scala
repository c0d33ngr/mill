package mill.integration

import mill.constants.Util
import mill.testkit.UtestIntegrationTestSuite
import utest._

object MillJvmOptsTests extends UtestIntegrationTestSuite {
  val tests: Tests = Tests {
    test("simple") - integrationTest { tester =>
      import tester._
      val res = eval("checkJvmOpts")
      assert(res.isSuccess)
    }
    test("interpolatedEnvVars") - integrationTest { tester =>
      if (!Util.isWindows) { // PWD does not exist on windows
        import tester._
        val res = eval(("show", "getEnvJvmOpts"))
        val out = res.out
        val expected = "\"value-with-" + tester.workspacePath + "\""
        assert(out == expected)
      }
    }
    test("nonJvmOpts") - integrationTest { tester =>
      import tester._
      val res = eval(("show", "getNonJvmOpts"))
      assert(res.out == "17")
    }
    test("overrideNonJvmOpts") - integrationTest { tester =>
      import tester._
      val res = eval(("--jobs", "19", "show", "getNonJvmOpts"))
      assert(res.out == "19")
    }
  }
}
