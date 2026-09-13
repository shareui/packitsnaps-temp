import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parent))
import compile_core
import compile_compose
import compile_packitutils

def before_validate_cruel(cfg_path, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_cruel(cfg_path, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_ref(cfg_path, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_ref(cfg_path, buildlog, references):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_pypi(cfg_path, cruel_bin, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_pypi(cfg_path, cruel_bin, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_whl(cfg_path, buildlog):
    try:
        return compile_packitutils.build_packutil(cfg_path, buildlog)
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_whl(cfg_path, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_syntax(pysrc_dir, cruel_bin, project_root, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_syntax(pysrc_dir, cruel_bin, project_root, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_imports(pysrc_dir, cruel_bin, project_root, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_imports(pysrc_dir, cruel_bin, project_root, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_validate_strings(cfg_path, cruel_bin, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_validate_strings(cfg_path, cruel_bin, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_generate_warn(pysrc_dir, cruel_bin, project_root, buildlog, cache):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_generate_warn(pysrc_dir, cruel_bin, project_root, buildlog, cache, total_warns, warn_files):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_compile_src(pysrc_dir, project_root, opt, pycompile, remove_pymeta, buildlog, cache, entry_path, cruel_bin):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_compile_src(pysrc_dir, project_root, opt, pycompile, remove_pymeta, buildlog, cache, entry_path, cruel_bin):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_pack_assets(cfg_path, project_root, buildlog, cache, cruel_bin):
    try:
        core_ok = compile_core.build_core_dex(project_root, buildlog, cache, cruel_bin)
        if not core_ok:
            return False
        return compile_compose.build_compose_dex(project_root, buildlog, cache, cruel_bin)
    except Exception as e:
        buildlog.error(f'before_pack_assets error: {e}')
        compile_core.clean_core_dex(project_root, buildlog)
        compile_compose.clean_compose_dex(project_root, buildlog)
        return False

def after_pack_assets(cfg_path, project_root, buildlog, cache, cruel_bin):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_pack_cruel(cfg_path, project_root, references, build_section, build_type, buildlog, cruel_bin):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_pack_cruel(cfg_path, project_root, references, build_section, build_type, buildlog, cruel_bin, temp_dir):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_link_sections(temp_dir, metadata, build_name, buildlog, cruel_bin):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_link_sections(temp_dir, metadata, build_name, buildlog, cruel_bin):
    try:
        project_root = Path(__file__).resolve().parent.parent.parent.parent
        compile_core.clean_core_dex(project_root, buildlog)
        compile_compose.clean_compose_dex(project_root, buildlog)
        compile_packitutils.clean_packutil(project_root, buildlog)
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_file_created(out_path, metadata, build_name, project_root, buildlog, arch=None):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def before_adb_push(out_path, metadata, build_name, project_root, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def after_adb_push(out_path, metadata, build_name, project_root, buildlog):
    try:
        return True
    except Exception as e:
        print(f'{e}')
        return False

def on_sigkill(buildlog):
    try:
        project_root = Path(__file__).resolve().parent.parent.parent.parent
        compile_core.clean_core_dex(project_root, buildlog)
        compile_compose.clean_compose_dex(project_root, buildlog)
        compile_packitutils.clean_packutil(project_root, buildlog)
        return True
    except Exception as e:
        print(f'{e}')
        return False
