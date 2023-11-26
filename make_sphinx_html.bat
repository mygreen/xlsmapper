@echo off

rem Sphinx�̃h�L�������g���r���h���Atareget��site�t�H���_�ɔz�u����B

echo "Building sphinx documentation with Docker."

%~d0
cd %~p0

rmdir /q /s .\src\site\sphinx\source\_build
docker run --rm -v %~dp0\src\site\sphinx\source:/docs xlsmapper/sphinx sphinx-build -M html . _build

rem sphinx�̐��ʕ��̃R�s�[
rmdir /q /s .\target\site\sphinx
mkdir .\target\site\sphinx
xcopy /y /e .\src\site\sphinx\source\_build\html .\target\site\sphinx

rem github-pages��sphinx�Ή�
echo "" > .\target\site\.nojekyll

pause
