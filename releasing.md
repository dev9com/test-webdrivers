## To Release follow these steps

1. Make sure everything is merged into master and all tests are passing.
2. Verify you have gpg installed with ```$ gpg --version```
3. Use the following settings.xml file
```
    <settings>
      <profiles>
        <profile>
          <id>test-webdrivers</id>
          <properties>
            <gpg.keyname>gpg key here</gpg.keyname>
            <gpg.passphrase>gpg password here</gpg.passphrase>
          </properties>
        </profile>
      </profiles>
      <servers>
        <server>
          <id>ossrh</id>
          <username>mvn central username</username>
          <password>mvn central password</password>
        </server>
      </servers>
    </settings>
```
4. Use an existing gpg key ```$ gpg --list-keys``` or create a new one and upload it ```$ gpg --gen-key``` 
then ```$ gpg --keyserver keyserver.ubuntu.com --send-keys GPG_KEY``` 
5. Add the uploaded key and password to the settings.xml file
6. Run ```$ mvn release:prepare -Darguments=-Dgpg.passphrase=GPG_PASSWORD``` if something fails use ```$ mvn release:rollback```
7. Run ```$ mvn release:perform -Darguments=-Dgpg.passphrase=GPG_PASSWORD```
8. Navigate to ```https://oss.sonatype.org/``` and sign in
9. Click Staging Repositories
10. Click the new repository (should have name like comdev9-num)
11. Click Close at top and verify everything passes
12. Click Release to public new artifact