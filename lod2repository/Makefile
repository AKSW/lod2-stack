default:
	@cat README

clean:
	@echo ""
	@echo "--> clean directory"
	rm -f trustdb.gpg secring.gpg *~
	rm -rf lod2repository*
	rm -rf lod2stable-repository*
	rm -rf lod2testing-repository*
	rm -rf lod2virt7-repository*
	rm -f *.log

list:
	gpg --no-permission-warning --homedir ./ --list-keys

upload-keys:
	ssh root@stack.lod2.eu "rm /home/packaging/.ssh/PublicKeys/*.pub"
	scp keys/*.pub root@stack.lod2.eu:/home/packaging/.ssh/PublicKeys/
	ssh root@stack.lod2.eu "cat /home/packaging/.ssh/PublicKeys/*.pub >/home/packaging/.ssh/authorized_keys"

sign-release:
	bin/sign-remote root@stack.lod2.eu:/var/mini-dinstall/lod2/Release

release-log-less:
	scp packaging@stack.lod2.eu:/var/mini-dinstall/mini-dinstall/mini-dinstall.log mini-dinstall.log
	less mini-dinstall.log
	rm mini-dinstall.log

release-ls-rejected:
	scp packaging@stack.lod2.eu:/var/mini-dinstall/mini-dinstall/REJECT.ls rejected.ls
	less rejected.ls
	rm rejected.ls

release-ls-incoming:
	scp packaging@stack.lod2.eu:/var/mini-dinstall/mini-dinstall/incoming.ls incoming.ls
	less incoming.ls
	rm incoming.ls

lod2repository: clean lod2repository-newversion lod2repository-build

lod2repository-newversion:
	@echo ""
	@echo "--> create new changelog entry"
	dch -D lod2 --force-distribution -v ChangeMe -b

LOD2REP_VERSION=$(shell dpkg-parsechangelog | grep Version: | head -1 | cut -d " " -f 2 | cut -d "-" -f 1)
lod2repository-build:
	@echo ""
	@echo "--> build the package directory"
	mkdir lod2repository-$(LOD2REP_VERSION)
	cp lod2.list lod2repository-$(LOD2REP_VERSION)
	cp pubring.gpg lod2repository-$(LOD2REP_VERSION)/lod2-keyring.gpg
	#tar cvjf lod2repository_$(LOD2REP_VERSION).orig.tar.bz2 lod2repository-$(LOD2REP_VERSION)
	cp -R debian lod2repository-$(LOD2REP_VERSION)
	cd lod2repository-$(LOD2REP_VERSION) && debuild && cd ..
	# testing
	mkdir -p lod2testing-repository-$(LOD2REP_VERSION)
	cp -R debian        lod2testing-repository-$(LOD2REP_VERSION)
	cp lod2testing.list lod2testing-repository-$(LOD2REP_VERSION)
	cp pubring.gpg      lod2testing-repository-$(LOD2REP_VERSION)/lod2-keyring.gpg
	cp lod2precise*     lod2testing-repository-$(LOD2REP_VERSION)
	cp install_testing  lod2testing-repository-$(LOD2REP_VERSION)/debian/lod2testing-repository.install
	cp prerm_testing  lod2testing-repository-$(LOD2REP_VERSION)/debian/prerm
	sed -i -e "s/lod2repository/lod2testing-repository/g" lod2testing-repository-$(LOD2REP_VERSION)/debian/changelog
	sed -i -e "s/lod2repository/lod2testing-repository/g" lod2testing-repository-$(LOD2REP_VERSION)/debian/control
	cd lod2testing-repository-$(LOD2REP_VERSION) && debuild
	# end testing
	# stable
	mkdir -p lod2stable-repository-$(LOD2REP_VERSION)
	cp -R debian        lod2stable-repository-$(LOD2REP_VERSION)
	cp lod2stable.list lod2stable-repository-$(LOD2REP_VERSION)
	cp pubring.gpg      lod2stable-repository-$(LOD2REP_VERSION)/lod2-keyring.gpg
	cp lod2precise*     lod2stable-repository-$(LOD2REP_VERSION)
	cp install_stable  lod2stable-repository-$(LOD2REP_VERSION)/debian/install
	cp install_stable  lod2stable-repository-$(LOD2REP_VERSION)/debian/lod2stable-repository.install
	cp prerm_stable  lod2stable-repository-$(LOD2REP_VERSION)/debian/prerm
	sed -i -e "s/lod2repository/lod2stable-repository/g" lod2stable-repository-$(LOD2REP_VERSION)/debian/changelog
	sed -i -e "s/lod2repository/lod2stable-repository/g" lod2stable-repository-$(LOD2REP_VERSION)/debian/control
	cd lod2stable-repository-$(LOD2REP_VERSION) && debuild
	# end stable
	# start virtv7
	mkdir -p lod2virt7-repository-$(LOD2REP_VERSION)
	cp -R debian        lod2virt7-repository-$(LOD2REP_VERSION)
	cp lod2virt7.* lod2virt7-repository-$(LOD2REP_VERSION)
	cp pubring.gpg      lod2virt7-repository-$(LOD2REP_VERSION)/lod2-keyring.gpg
	cp install_virt7  lod2virt7-repository-$(LOD2REP_VERSION)/debian/install
	cp install_virt7  lod2virt7-repository-$(LOD2REP_VERSION)/debian/lod2virt7-repository.install
	cp prerm_virt7  lod2virt7-repository-$(LOD2REP_VERSION)/debian/prerm
	sed -i -e "s/lod2repository/lod2virt7-repository/g" lod2virt7-repository-$(LOD2REP_VERSION)/debian/changelog
	sed -i -e "s/lod2repository/lod2virt7-repository/g" lod2virt7-repository-$(LOD2REP_VERSION)/debian/control
	cd lod2virt7-repository-$(LOD2REP_VERSION) && debuild
	# end virtv7
	@echo ""
	@echo "now test the package, then run \"dput lod2 lod2repository_xxx.changes\""
	@echo "now test the package, then run \"dput lod2 lod2testing-repository_xxx.changes\""
	@echo "now test the package, then run \"dput lod2 lod2stable-repository_xxx.changes\""

