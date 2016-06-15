# securemock
Allows creating mocks in tests without having to grant dangerous permissions to all of your code.

This wraps the mockito api with AccessController blocks. No code changes are needed.

Instead of:

    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
      <version>1.9.5</version>
      <scope>test</scope>
    </dependency>
    ...
    grant {
      // give scary permission to all code just for tests
      permission java.lang.RuntimePermission "accessClassInPackage.sun.reflect";
      permission java.lang.RuntimePermission "accessClassInPackage.sun.misc";
      permission java.lang.RuntimePermission "reflectionFactoryAccess";
      permission java.lang.RuntimePermission "accessDeclaredMembers";
      permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
    };

You can do:

    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>securemock</artifactId>
      <version>1.2</version>
      <scope>test</scope>
    </dependency>
    ...
    grant codeBase "/url/to/securemock.jar" {
      // only allow this jar used in tests to do this
      permission java.lang.RuntimePermission "accessClassInPackage.sun.reflect";
      permission java.lang.RuntimePermission "accessClassInPackage.sun.misc";
      permission java.lang.RuntimePermission "reflectionFactoryAccess";
      permission java.lang.RuntimePermission "accessDeclaredMembers";
      permission java.lang.reflect.ReflectPermission "suppressAccessChecks";
    };
