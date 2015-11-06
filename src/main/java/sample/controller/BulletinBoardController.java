package sample.controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import sample.exception.SystemErrorException;
import sample.form.BulletinBoardForm;
import sample.form.PageCondition;
import sample.model.BulletinBoardData;
import sample.service.BulletinBoardDataService;

@Controller
@EnableAutoConfiguration
@RequestMapping("/bulletinBoard")
public class BulletinBoardController {

    private static final Logger log = LoggerFactory.getLogger(BulletinBoardController.class);

    @Autowired
    BulletinBoardDataService bulletinBoardDataService;

    @ModelAttribute // 画面で使うフォームに対応したオブジェクトを初期化して、Modelに追加する(Thymeleafからアクセスさせるために必要)
    BulletinBoardForm setupForm() {
        return new BulletinBoardForm();
    }

    /**
     * 全件取得
     *
     * DBから全データを取得します
     *
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/serch/list", method = RequestMethod.GET)
    public String serchList(Locale locale, Model model) {
        getPageData(model);
        return "bulletinBoard";
    }

    /**
     * 指定したページのデータを取得する
     *
     *
     */
    @RequestMapping(value = "/serch/page", method = RequestMethod.GET)
    public String getPageData(@RequestParam int page, Locale locale, Model model) {
        PageCondition pageCondition = bulletinBoardDataService.getPageCondition(page);

        List<BulletinBoardData> list = bulletinBoardDataService.getAssignPageData(pageCondition);
        model.addAttribute("bulletinBoardDataList", list);
        model.addAttribute("pageCondition", pageCondition);

        return "bulletinBoard";
    }

    /**
     * パラメータで指定された名前のデータを検索する
     *
     * @param bulletinBoardForm フォームデータ
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/serch", method = RequestMethod.POST)
    public String serchKeyword(BulletinBoardForm bulletinBoardForm, Locale locale, Model model) {
        PageCondition pageCondition = bulletinBoardDataService.getPageCondition(1);

        List<BulletinBoardData> list = bulletinBoardDataService.getSearchNameBulletinBoardData(bulletinBoardForm.getName(), pageCondition);

        if(list.isEmpty()) {
            //　該当ユーザーが存在しない場合のエラーを返却する

        }

        model.addAttribute("bulletinBoardDataList", list);
        model.addAttribute("pageCondition", pageCondition);
        model.addAttribute("bulletinBoardForm", new BulletinBoardForm());

        return "bulletinBoard";
    }

    /**
     * 画面で入力されたデータをDBに登録して今回登録したデータを含めて全データを画面に返却する
     *
     * ※トランザクションの範囲について確認しておく
     *
     * @param bulletinBoardForm フォームデータ
     * @param locale
     * @param model
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String registReport(@Valid BulletinBoardForm bulletinBoardForm, BindingResult result, Model model) {

        // バリデータ
        if (result.hasErrors()) {
            for(FieldError err: result.getFieldErrors()) {
                log.info("error code = [" + err.getCode() + "]");
            }
            getPageData(model);
            return "bulletinBoard";
        }

        try {
            BulletinBoardData entity = new BulletinBoardData();
            entity.setName(bulletinBoardForm.getName());
            entity.setPostingContent(bulletinBoardForm.getPostingContent());
            entity.setPostingDate(new Timestamp(System.currentTimeMillis()));
            entity.setRegisterDate(new Timestamp(System.currentTimeMillis()));
            bulletinBoardDataService.addBulletinBoardData(entity);
        } catch(Exception e) {
            log.info(e.getMessage());
            throw new SystemErrorException();
        }

        getPageData(model);
        model.addAttribute("bulletinBoardForm", new BulletinBoardForm());
        return "bulletinBoard";
    }

    private void getDataAll(Model model) {
        List<BulletinBoardData> list = bulletinBoardDataService.getBulletinBoardDataAll();
        model.addAttribute("bulletinBoardDataList", list);
    }

    private void getPageData(Model model) {
        PageCondition pageCondition = bulletinBoardDataService.getPageCondition(1);

        List<BulletinBoardData> list = bulletinBoardDataService.getAssignPageData(pageCondition);
        model.addAttribute("bulletinBoardDataList", list);
        model.addAttribute("pageCondition", pageCondition);
    }
}