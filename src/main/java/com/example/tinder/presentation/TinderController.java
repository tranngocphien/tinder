package com.example.tinder.presentation;

import com.example.tinder.application.HbaseManageTable;
import com.example.tinder.application.MessageSender;
import com.example.tinder.domain.*;
import com.example.tinder.infrastucture.*;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class TinderController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InteractiveRepository interactiveRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/listUser")
    public List<User> getListUser() {
        return userRepository.findAll();

    }

    @PostMapping("/register")
    public ResponseEntity add(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().build();
        }
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/allinteractive")
    public List<Interactive> getAllInteractive() {
        return interactiveRepository.findAll();
    }

    @PostMapping("/interactive")
    public ResponseEntity addInteractive(@RequestBody Interactive interactive) {
        interactive.setStatus(1);
        interactiveRepository.save(interactive);
        Activity activity = new Activity();
        activity.setType(ActivityType.FOLLOW);
        activity.setFrom_userid(interactive.getInteractiveID().getFromUserID());
        activity.setTo_userid(interactive.getInteractiveID().getToUserID());
        messageSender.sendmessage(activity);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/interactive")
    public ResponseEntity deleteInteractive(@RequestBody Interactive interactive) {
        interactive.setStatus(0);
        interactiveRepository.save(interactive);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getstatus")
    public Interactive getStatus(@RequestBody InteractiveID interactiveID) {
        return interactiveRepository.findAllByInteractiveID(interactiveID);
    }

    @GetMapping("/ismatched")
    public boolean getMatched(@RequestBody InteractiveID interactiveID) {
        if (interactiveRepository.findAllByInteractiveID(interactiveID) == null) {
            return false;
        }
        if (interactiveRepository.findAllByInteractiveID(new InteractiveID(interactiveID.getToUserID(), interactiveID.getFromUserID())) == null) {
            return false;
        }
        return true;
    }

    @GetMapping("/get")
    public Response getmessage(@RequestBody InteractiveID interactiveID) {
        Response response = new Response();
        Interactive interactive = interactiveRepository.findAllByInteractiveID(interactiveID);
        response.setInteractive(interactive);
        if (interactive == null) {
            response.setMessage("Khong ton tai");
        } else if (interactiveRepository.findAllByInteractiveID(new InteractiveID(interactiveID.getToUserID(), interactiveID.getFromUserID())) != null) {
            response.setMessage("Hop nhau");
        } else {
            response.setMessage("Thich");
        }
        return response;
    }

    @PostMapping("/upImage/{id}")
    public String upImage(@RequestBody MultipartFile file, @PathVariable(name = "id") int id) {
        String encodefile = null;
        try {
            byte[] bytes = file.getBytes();
            encodefile = Base64.getEncoder().encodeToString(bytes);
            User user = userRepository.findById(id);
            user.setAvatar(encodefile);
            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodefile;
    }

    @GetMapping(value = "/avatar/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] avatar(@PathVariable(name = "id") int id) {
        User user = userRepository.findById(id);
        byte[] bytes = Base64.getDecoder().decode(user.getAvatar());
        return bytes;
    }
    // Up status
    @PostMapping(value = "/upstatus/{id}")
    public void upStatus(@RequestBody MultipartFile file, String text, @PathVariable(name = "id") int id) {
        System.out.println(id);
        try {
            byte[] bytes = file.getBytes();
            Status status = new Status();
            status.setUserID(id);
            status.setImage(Base64.getEncoder().encodeToString(bytes));
            status.setText(text);
            statusRepository.save(status);
            Activity activity = new Activity();
            activity.setType(ActivityType.UP_STATUS);
            activity.setFrom_userid(id);
            messageSender.sendmessage(activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete status
    @DeleteMapping(value = "/deletestatus/{id}")
    public void deleteStatus(@PathVariable(name = "id") int idStatus){
        if(statusRepository.findAllByStatusID(idStatus) != null)
            statusRepository.deleteById(idStatus);
    }

    // List status theo iduser(khong comment,like)
    @GetMapping(value = "/liststatus/{id}")
    public List<Status> getStatus(@PathVariable(name = "id") int id) {
        return statusRepository.findAllByUserID(id);
    }


    @GetMapping(value = "/liststatus")
    public Status getliststt() {
        return statusRepository.findAllByStatusID(7);

    }

    // Like status
    @PostMapping(value = "/likestatus")
    public void likeStatus(@RequestBody Like1 like1) {
        like1.setLikeID(0);
        likeRepository.save(like1);
        Activity activity = new Activity();
        activity.setType(ActivityType.LIKE_STATUS);
        activity.setIdStatus(like1.getStatusID());
        activity.setFrom_userid(like1.getIdUser());
        System.out.println(like1.getStatusID());
        activity.setTo_userid(statusRepository.findAllByStatusID(like1.getStatusID()).getUserID());
        messageSender.sendmessage(activity);
    }

    // Delete like status
    @DeleteMapping("/deletelike")
    public void deleteLike(@RequestBody Like1 like1) {
        likeRepository.delete(like1);
    }

    // COMMENT
    // Thêm comment
    @PostMapping(value = "/addCommentStatus")
    public void commentStatus(@RequestBody Comment comment) {
        comment.setTime(new Timestamp(System.currentTimeMillis()));
        commentRepository.save(comment);
        Activity activity = new Activity();
        activity.setType(ActivityType.COMMENT_STATUS);
        activity.setIdStatus(comment.getStatusID());
        activity.setFrom_userid(comment.getIdUser());
        activity.setTo_userid(statusRepository.findAllByStatusID(comment.getStatusID()).getUserID());
        messageSender.sendmessage(activity);
    }

    // thêm comment (hbase)
    @PostMapping(value = "addComment")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) throws NoSuchAlgorithmException, IOException {
        SequenceGenerator sequenceGenerator = new SequenceGenerator(1);
        comment.setCommentID(sequenceGenerator.nextID());
        byte[] rowkey = HbaseManageTable.generateRowkey(comment.getCommentID());
        String zkHosts = "adt-ml-hbase-dev-104-93";
        int zkPort = 2181;
        String zkBasePath = "/hbase-unsecure";
        HbaseAdapter hbaseAdapter = new HbaseAdapter(zkHosts, zkPort, zkBasePath);
        Connection connection = hbaseAdapter.getConnection();
        comment.setTime(new Timestamp(System.currentTimeMillis()));
        HbaseManageTable.insertData(connection,TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"),Bytes.toBytes("statusId"),Bytes.toBytes(comment.getStatusID()), rowkey);
        HbaseManageTable.insertData(connection,TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"),Bytes.toBytes("userId"),Bytes.toBytes(comment.getIdUser()), rowkey);
        HbaseManageTable.insertData(connection,TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"),Bytes.toBytes("content"),Bytes.toBytes(comment.getContent()), rowkey);
        HbaseManageTable.insertData(connection,TableName.valueOf("tinder", "comment"),Bytes.toBytes("cf"),Bytes.toBytes("time"),Bytes.toBytes(comment.getTime().getTime()), rowkey);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping(value = "getComment/{id}")
    public List<Comment> getcomment(@PathVariable(name = "id") int idStatus) throws IOException {
        String zkHosts = "adt-ml-hbase-dev-104-93";
        int zkPort = 2181;
        String zkBasePath = "/hbase-unsecure";
        HbaseAdapter hbaseAdapter = new HbaseAdapter(zkHosts, zkPort, zkBasePath);
        ResultScanner results = HbaseManageTable.scanValueColumn(hbaseAdapter.getConnection(),TableName.valueOf("tinder", "comment"), Bytes.toBytes("cf"), Bytes.toBytes("statusId"), Bytes.toBytes(idStatus));
        List<Comment> list = new ArrayList<>();
        for (Result result = results.next(); result != null; result = results.next()) {
            //System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("statusId"))));
            byte[] rowkey = result.getRow();
            String row = Bytes.toString(rowkey);
            String id = row.substring(row.indexOf("|"));
            Comment comment = new Comment();
            comment.setCommentID(Bytes.toLong(Bytes.toBytes(id)));
            comment.setStatusID(Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("statusId"))));
            comment.setIdUser(Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("userId"))));
            comment.setContent(Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("content"))));
            comment.setTime(new Timestamp(Bytes.toLong(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("time")))));
            System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("statusId"))));
            System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("userId"))));
            System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("content"))));
            System.out.println(Bytes.toLong(result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("time"))));
            list.add(comment);

        }
        return list;
    }



    // Xóa comment
    @DeleteMapping(value = "/deleteComment/{id}")
    public ResponseEntity<Comment> deleteComment(@PathVariable(name = "id") int id) {
        if (commentRepository.findByCommentID(id) == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            commentRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    // Thay đổi comment
    @PutMapping(value = "/changeComment")
    public ResponseEntity<Comment> changeComment(@RequestBody Comment comment) {
        comment.setTime(new Timestamp(System.currentTimeMillis()));
        if (commentRepository.findByCommentID(comment.getCommentID()) != null) {
            commentRepository.save(comment);
            return new ResponseEntity<Comment>(comment, HttpStatus.OK);
        } else {
            return new ResponseEntity<Comment>(comment, HttpStatus.NO_CONTENT);
        }

    }

    // GetListStatus
    // Lấy tất cả status của 1 user
    @GetMapping(value = "/getStatus/{id}")
    public List<StatusDetail> getListStatus(@PathVariable(name = "id") int id) {
        List<StatusDetail> list = new ArrayList<>();
        List<Status> listStatus = statusRepository.findAllByUserID(id);
        for (Status status : listStatus) {
            StatusDetail detail = new StatusDetail();
            detail.setStatus(status);

            int statusID = status.getStatusID();
            detail.setLikeList(likeRepository.findAllByStatusID(statusID));
            List<Comment> comments = commentRepository.findAllByStatusID(statusID, PageRequest.of(0, 10, Sort.by("time").descending()));
            detail.setListComment(comments);
            list.add(detail);
        }
        return list;
    }

    @PostMapping(value = "/likecomment")
    public void likeComment(@RequestBody LikeComment likeComment){

    }

}
